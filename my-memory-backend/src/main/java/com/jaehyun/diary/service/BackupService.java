package com.jaehyun.diary.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class BackupService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final String uploadDir = "uploads";

    /**
     * Creates a backup zip containing:
     * - backup.json (serialized list of DiaryEntity)
     * - images.zip (zip file containing all uploads)
     */
    public void createBackup(OutputStream outputStream) throws IOException {
        Path tempDir = Files.createTempDirectory("diary_backup_");
        Path backupJsonPath = tempDir.resolve("backup.json");
        Path imagesZipPath = tempDir.resolve("images.zip");

        try {
            // 1. Serialize database diaries to backup.json
            List<DiaryEntity> diaries = diaryRepository.findAll();
            objectMapper.writeValue(backupJsonPath.toFile(), diaries);

            // 2. Compress uploads directory to images.zip
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(imagesZipPath))) {
                Path uploadsPath = Paths.get(uploadDir);
                if (Files.exists(uploadsPath) && Files.isDirectory(uploadsPath)) {
                    try (var stream = Files.walk(uploadsPath)) {
                        List<Path> files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
                        for (Path file : files) {
                            String name = file.getFileName().toString();
                            ZipEntry entry = new ZipEntry(name);
                            zos.putNextEntry(entry);
                            Files.copy(file, zos);
                            zos.closeEntry();
                        }
                    }
                }
            }

            // 3. Put both backup.json and images.zip into the outer zip
            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                // Add backup.json
                ZipEntry jsonEntry = new ZipEntry("backup.json");
                zos.putNextEntry(jsonEntry);
                Files.copy(backupJsonPath, zos);
                zos.closeEntry();

                // Add images.zip
                ZipEntry imagesEntry = new ZipEntry("images.zip");
                zos.putNextEntry(imagesEntry);
                Files.copy(imagesZipPath, zos);
                zos.closeEntry();
            }
        } finally {
            // Clean up temp files
            deleteDirectoryRecursive(tempDir);
        }
    }

    /**
     * Restores backup from zip file:
     * - Parses and validates backup.json
     * - Extracts images.zip
     * - Wipes database and replaces with backup.json records
     * - Wipes uploads/ folder and replaces with files from images.zip
     * - Restores original state if any step fails (rollback)
     */
    public void restoreBackup(InputStream zipInputStream) throws IOException {
        Path tempDir = Files.createTempDirectory("diary_restore_");
        Path backupJsonPath = tempDir.resolve("backup.json");
        Path imagesZipPath = tempDir.resolve("images.zip");
        Path tempImagesExtractDir = Files.createTempDirectory("diary_restore_images_");
        Path tempUploadsBackupDir = Files.createTempDirectory("diary_uploads_backup_");

        try {
            // 1. Extract outer zip contents to temporary directory
            try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if ("backup.json".equals(entry.getName())) {
                        Files.copy(zis, backupJsonPath, StandardCopyOption.REPLACE_EXISTING);
                    } else if ("images.zip".equals(entry.getName())) {
                        Files.copy(zis, imagesZipPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }

            // 2. Validate backup.json existence and parser compatibility
            if (!Files.exists(backupJsonPath)) {
                throw new FileNotFoundException("backup.json 파일이 존재하지 않는 압축 파일입니다.");
            }
            List<DiaryEntity> newDiaries;
            try {
                newDiaries = objectMapper.readValue(backupJsonPath.toFile(), new TypeReference<List<DiaryEntity>>() {});
            } catch (Exception e) {
                throw new IllegalArgumentException("backup.json 데이터 파싱에 실패했습니다: " + e.getMessage(), e);
            }

            // 3. Extract images.zip contents to temp directory if exists
            if (Files.exists(imagesZipPath)) {
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(imagesZipPath))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        String name = entry.getName();
                        Path targetPath = tempImagesExtractDir.resolve(name).normalize();
                        if (!targetPath.startsWith(tempImagesExtractDir)) {
                            throw new SecurityException("압축 파일 내의 비정상적인 파일 경로가 감지되었습니다: " + name);
                        }
                        Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        zis.closeEntry();
                    }
                }
            }

            // 4. Back up current Database and uploads/ directory for rollback
            List<DiaryEntity> originalDiaries = diaryRepository.findAll();
            Path uploadsPath = Paths.get(uploadDir);
            if (Files.exists(uploadsPath) && Files.isDirectory(uploadsPath)) {
                try (var stream = Files.walk(uploadsPath)) {
                    List<Path> files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
                    for (Path f : files) {
                        Path relative = uploadsPath.relativize(f);
                        Path backupDest = tempUploadsBackupDir.resolve(relative);
                        Files.createDirectories(backupDest.getParent());
                        Files.copy(f, backupDest, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            // 5. Try DB and Disk modification
            try {
                // Wipe DB and insert new diaries
                diaryRepository.deleteAll();
                diaryRepository.saveAll(newDiaries);

                // Wipe existing uploads directory files
                if (Files.exists(uploadsPath) && Files.isDirectory(uploadsPath)) {
                    try (var stream = Files.walk(uploadsPath)) {
                        List<Path> files = stream.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                        for (Path f : files) {
                            if (!f.equals(uploadsPath)) {
                                Files.delete(f);
                            }
                        }
                    }
                } else {
                    Files.createDirectories(uploadsPath);
                }

                // Copy new extracted images to uploads/
                if (Files.exists(tempImagesExtractDir)) {
                    try (var stream = Files.walk(tempImagesExtractDir)) {
                        List<Path> files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
                        for (Path f : files) {
                            Path relative = tempImagesExtractDir.relativize(f);
                            Path dest = uploadsPath.resolve(relative);
                            Files.createDirectories(dest.getParent());
                            Files.copy(f, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
                log.info("Backup restored successfully.");
            } catch (Exception e) {
                log.error("Error occurred during restore, rolling back changes...", e);
                // Perform Rollback
                try {
                    diaryRepository.deleteAll();
                    diaryRepository.saveAll(originalDiaries);

                    if (Files.exists(uploadsPath) && Files.isDirectory(uploadsPath)) {
                        try (var stream = Files.walk(uploadsPath)) {
                            List<Path> files = stream.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                            for (Path f : files) {
                                if (!f.equals(uploadsPath)) {
                                    Files.delete(f);
                                }
                            }
                        }
                    }

                    if (Files.exists(tempUploadsBackupDir)) {
                        try (var stream = Files.walk(tempUploadsBackupDir)) {
                            List<Path> files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
                            for (Path f : files) {
                                Path relative = tempUploadsBackupDir.relativize(f);
                                Path dest = uploadsPath.resolve(relative);
                                Files.createDirectories(dest.getParent());
                                Files.copy(f, dest, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                    log.info("Rollback completed successfully.");
                } catch (Exception re) {
                    log.error("Critical: Failed to roll back database or images directory!", re);
                }
                throw new RuntimeException("백업 복원 중 오류가 발생하여 기존 상태로 롤백되었습니다: " + e.getMessage(), e);
            }

        } finally {
            // Clean up all temporary folders
            deleteDirectoryRecursive(tempDir);
            deleteDirectoryRecursive(tempImagesExtractDir);
            deleteDirectoryRecursive(tempUploadsBackupDir);
        }
    }

    private void deleteDirectoryRecursive(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.error("Failed to delete temporary directory: {}", path, e);
        }
    }
}
