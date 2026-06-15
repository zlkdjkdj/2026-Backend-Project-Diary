package com.jaehyun.diary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
public class S3ConnectionTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private com.jaehyun.diary.repository.DiaryRepository diaryRepository;

    @Autowired
    private com.jaehyun.diary.service.FileService fileService;

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

    @Test
    public void testMongoQuery() {
        System.out.println("몽고DB 조회 테스트 시작...");
        try {
            var diaries = diaryRepository.findAll();
            System.out.println("조회 성공! 일기 개수: " + diaries.size());
            for (var d : diaries) {
                System.out.println("일기 제목: " + d.getDiaryTitle() + ", 날짜: " + d.getWrittenDate());
            }
        } catch (Exception e) {
            System.err.println("몽고DB 조회 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testFileUpload() throws Exception {
        System.out.println("S3 파일 업로드 테스트 시작...");
        try {
            org.springframework.mock.web.MockMultipartFile mockFile = new org.springframework.mock.web.MockMultipartFile(
                    "image",
                    "test-image.png",
                    "image/png",
                    "test image content".getBytes()
            );
            String url = fileService.saveFile(mockFile);
            System.out.println("업로드 성공! 파일 URL: " + url);
            assertThat(url).isNotNull();
            assertThat(url).contains("amazonaws.com");
        } catch (Exception e) {
            System.err.println("S3 파일 업로드 실패!");
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testGetAllDiariesApi() throws Exception {
        System.out.println("API GET /api/diary 호출 테스트 시작...");
        try {
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/diary")
                    .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
            System.out.println("GET API 호출 성공!");
        } catch (Exception e) {
            System.err.println("GET API 호출 실패!");
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testCreateDiaryApi() throws Exception {
        System.out.println("API POST /api/diary 호출 테스트 시작...");
        try {
            String diaryJson = "{\"diaryTitle\":\"테스트 제목\",\"diaryContent\":\"테스트 내용\",\"writtenDate\":\"2026-06-15\"}";
            org.springframework.mock.web.MockMultipartFile diaryPart = new org.springframework.mock.web.MockMultipartFile(
                    "diary",
                    "",
                    "application/json",
                    diaryJson.getBytes()
            );

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/diary")
                    .file(diaryPart)
                    .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
            System.out.println("POST API 호출 성공!");
        } catch (Exception e) {
            System.err.println("POST API 호출 실패!");
            e.printStackTrace();
            throw e;
        }
    }
}
