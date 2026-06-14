package com.jaehyun.diary.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// 파일 시스템 I/O 전담 서비스 클래스
@Service
public class FileService {

    // 서버 내부에 파일이 저장될 기준 디렉토리 경로 (문자열)
    private final String uploadDir = "uploads";

    // 로컬 시스템에 멀티파트 파일 저장 및 접근 경로 반환
    public String saveFile(MultipartFile file) throws IOException {
        // 파일 객체가 비어있거나 정상적으로 전달되지 않았을 경우 무시하고 null 반환
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 저장 대상 디렉토리가 존재하지 않을 경우 새로 생성
        // 문자열 경로(uploadDir)를 기반으로 파일 시스템의 디렉토리를 제어하기 위해 생성한 File 객체
        File directory = new File(uploadDir);
        // 디렉토리가 실제 파일 시스템에 존재하지 않는다면 mkdirs()를 호출하여 물리적 디렉토리 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 이름 충돌 방지
        // 클라이언트가 업로드한 파일의 원래 이름
        String originalFilename = file.getOriginalFilename();
        // 확장자명만 추출하여 보관할 임시 문자열
        String fileExtension = "";
        // 원본 파일명에 확장자(.)가 존재하는지 확인하여 분리
        if (originalFilename != null && originalFilename.contains(".")) {
            // 마지막 '.' 이후의 문자열을 추출하여 확장자로 사용 (예: .jpg, .png)
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 지정된 경로로 물리적 파일 스트림 쓰기 작업 수행
        Path path = Paths.get(directory.getAbsolutePath(), newFilename);
        // 메모리에 로드된 MultipartFile의 바이트 배열(데이터)을 실제 디스크의 해당 경로(path)에 파일
        Files.write(path, file.getBytes());

        // 저장 완료 후 정적 리소스 접근 경로 반환
        // WebConfig 등에 의해 정적 리소스로 매핑된 '/uploads/' URL 뒤에 새 파일명을 붙여 반환
        return "/uploads/" + newFilename;
    }
}
