package com.jaehyun.diary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// S3 설정
@Configuration
public class S3Config {

    @Value("${AWS_ACCESS_KEY_ID:}") // accesskey
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY:}") // secretkey
    private String secretKey;

    @Value("${AWS_REGION:ap-northeast-2}") // region
    private String region;

    @Bean
    public S3Client s3Client() {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            // aws자격 증명이 없는경우 default 클라이언트 반환
            return S3Client.builder().region(Region.of(region)).build();
        }

        // 명시된 Access Key와 Secret Key로 자격 증명 생성 후 클라이언트 반환
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey); // aws인증 객체 생성
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
