package com.jaehyun.diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.util.UUID;

// 파일 서비스: AWS S3 버킷에 파일을 업로드하고 관리하는 서비스 클래스
@Service
public class FileService {

    @Autowired
    private S3Client s3Client; // S3Config에서 생성된 S3Client 빈 주입

    @Value("${AWS_S3_BUCKET:}")
    private String bucketName; // application.properties/yml 또는 환경 변수에서 버킷 이름 로드

    /**
     * 멀티파트 파일을 받아 S3 버킷에 업로드하고, 저장된 파일의 Public URL을 반환합니다.
     */
    public String saveFile(MultipartFile file) throws IOException {
        // 업로드할 파일이 비어있는 경우 null 반환
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 파일명 고유화 (중복 방지를 위한 UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            // 원본 파일의 확장자 추출 (예: .png, .jpg)
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID와 확장자를 결합하여 고유한 파일명 생성 (예: e4d8c6b7-f83a-4a2c-a2b1-9b16822c9e78.jpg)
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 2. MIME 타입 설정 (브라우저에서 바로 열거나 올바르게 해석할 수 있도록 설정)
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream"; // 알 수 없는 이진 파일용 기본값
        }

        // 3. S3 업로드 요청 객체 생성 (버킷명, 파일 키, 컨텐츠 타입, 읽기 권한 설정)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(newFilename)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ) // 외부 브라우저에서 URL로 바로 읽을 수 있게 허용 (Public)
                .build();

        // 4. S3 클라이언트를 통해 실제 파일 데이터 전송
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // 5. 업로드된 파일의 S3 Public URL 반환
        return "https://" + bucketName + ".s3.amazonaws.com/" + newFilename;
    }
}

