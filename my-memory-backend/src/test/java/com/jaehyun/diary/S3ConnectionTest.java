package com.jaehyun.diary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class S3ConnectionTest {

    @Autowired
    private S3Client s3Client;

    @Test
    public void testS3Connection() {
        System.out.println("S3 연결 테스트 시작...");
        try {
            ListBucketsResponse response = s3Client.listBuckets();
            System.out.println("연결 성공! S3 버킷 개수: " + response.buckets().size());
            assertThat(response).isNotNull();
        } catch (Exception e) {
            System.err.println("S3 연결 실패: " + e.getMessage());
            throw e;
        }
    }
}
