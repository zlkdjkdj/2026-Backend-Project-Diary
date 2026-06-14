package com.jaehyun.diary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

// 시스템 데이터 백업 및 복원 기능 서비스 클래스
@Slf4j
@Service
public class BackupService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String uploadDir = "uploads";

    // DB 레코드 및 업로드 이미지 기반 ZIP 아카이브 생성
    public void createBackup(OutputStream outputStream) throws IOException {
        Path tempDir = Files.createTempDirectory("diary_backup_");
        Path backupJsonPath = tempDir.resolve("backup.json");
        Path imagesZipPath = tempDir.resolve("images.zip");

        try {
            // 1. DB 데이터를 JSON으로 변환하여 임시 저장
            objectMapper.writeValue(backupJsonPath.toFile(), diaryRepository.findAll());

            // 2. 업로드된 이미지 파일들을 ZIP으로 압축
            Path uploadsPath = Paths.get(uploadDir);
            if (Files.exists(uploadsPath)) {
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(imagesZipPath));
                        var stream = Files.walk(uploadsPath)) {
                    stream.filter(Files::isRegularFile).forEach(file -> {
                        try {
                            zos.putNextEntry(new ZipEntry(uploadsPath.relativize(file).toString()));
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                }
            }

            // 3. JSON 파일과 이미지 ZIP 파일을 최종 아카이브로 통합
            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                zos.putNextEntry(new ZipEntry("backup.json"));
                Files.copy(backupJsonPath, zos);
                zos.closeEntry();

                if (Files.exists(imagesZipPath)) {
                    zos.putNextEntry(new ZipEntry("images.zip"));
                    Files.copy(imagesZipPath, zos);
                    zos.closeEntry();
                }
            }
        } finally {
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }

    // 제공된 ZIP 아카이브 기반 시스템 상태 복원
    public void restoreBackup(InputStream zipInputStream) throws IOException {
        Path tempDir = Files.createTempDirectory("diary_restore_");
        Path tempImagesExtractDir = Files.createTempDirectory("diary_restore_images_");
        Path tempUploadsBackupDir = Files.createTempDirectory("diary_uploads_backup_");

        Path backupJsonPath = tempDir.resolve("backup.json");
        Path imagesZipPath = tempDir.resolve("images.zip");

        try {
            // 1. 전달받은 ZIP 아카이브 해제 (JSON, 이미지 ZIP 분리)
            try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals("backup.json")) {
                        Files.copy(zis, backupJsonPath, StandardCopyOption.REPLACE_EXISTING);
                    } else if (entry.getName().equals("images.zip")) {
                        Files.copy(zis, imagesZipPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            if (!Files.exists(backupJsonPath)) {
                throw new FileNotFoundException("backup.json 파일 누락");
            }
            List<DiaryEntity> newDiaries = objectMapper.readValue(backupJsonPath.toFile(), new TypeReference<>() {
            });

            // 2. 이미지 ZIP 파일 해제
            if (Files.exists(imagesZipPath)) {
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(imagesZipPath))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        Path targetPath = tempImagesExtractDir.resolve(entry.getName()).normalize();
                        if (!targetPath.startsWith(tempImagesExtractDir)) {
                            throw new SecurityException("비정상적인 경로 감지: " + entry.getName());
                        }
                        Files.createDirectories(targetPath.getParent());
                        Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            // 3. 복원 실패 시 롤백을 위한 기존 데이터 백업
            List<DiaryEntity> originalDiaries = diaryRepository.findAll();
            Path uploadsPath = Paths.get(uploadDir);
            if (Files.exists(uploadsPath)) {
                FileSystemUtils.copyRecursively(uploadsPath, tempUploadsBackupDir);
            }

            // 4. 기존 데이터 삭제 및 신규 데이터 적재 (트랜잭션)
            try {
                diaryRepository.deleteAll();
                diaryRepository.saveAll(newDiaries);

                FileSystemUtils.deleteRecursively(uploadsPath);
                Files.createDirectories(uploadsPath);
                if (Files.exists(tempImagesExtractDir)) {
                    FileSystemUtils.copyRecursively(tempImagesExtractDir, uploadsPath);
                }

                log.info("백업 데이터 복원 성공");
            } catch (Exception e) {
                log.error("복원 중 오류 발생. 원상 복구를 시도합니다...", e);
                // 롤백 수행
                diaryRepository.deleteAll();
                diaryRepository.saveAll(originalDiaries);

                FileSystemUtils.deleteRecursively(uploadsPath);
                if (Files.exists(tempUploadsBackupDir)) {
                    FileSystemUtils.copyRecursively(tempUploadsBackupDir, uploadsPath);
                }
                throw new RuntimeException("복원 실패로 롤백 되었습니다: " + e.getMessage(), e);
            }

        } finally {
            // 5. 사용한 임시 리소스 정리
            FileSystemUtils.deleteRecursively(tempDir);
            FileSystemUtils.deleteRecursively(tempImagesExtractDir);
            FileSystemUtils.deleteRecursively(tempUploadsBackupDir);
        }
    }
}
