package com.jaehyun.diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

// S3 파일 업로드 전담 서비스 클래스
@Service
public class FileService {

    @Autowired
    private S3Client s3Client;

    @Value("${AWS_S3_BUCKET:}")
    private String bucketName;

    // 전달받은 멀티파트 파일을 S3 버킷에 업로드하고 외부 접근 URL 반환
    public String saveFile(MultipartFile file) throws IOException {
        // 파일 객체가 비어있거나 정상적으로 전달되지 않았을 경우 무시하고 null 반환
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 파일 이름 충돌 방지
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // ContentType 처리
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // S3에 파일 업로드 수행
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(newFilename)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // 저장 완료 후 S3 접근 URL 반환
        return "https://" + bucketName + ".s3.amazonaws.com/" + newFilename;
    }
}

