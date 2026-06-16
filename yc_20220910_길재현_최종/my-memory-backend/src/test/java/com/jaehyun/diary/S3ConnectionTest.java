package com.jaehyun.diary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@SuppressWarnings("null")
public class S3ConnectionTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private com.jaehyun.diary.repository.DiaryRepository diaryRepository;

    @Autowired
    private com.jaehyun.diary.service.FileService fileService;

    @Autowired
    private com.jaehyun.diary.service.AuthService authService;

    @Autowired
    private com.jaehyun.diary.repository.UserRepository userRepository;

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
            String responseBody = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/diary")
                    .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println("GET API 호출 성공! 응답 본문: " + responseBody);
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
            // 기존 테스트 데이터 클린업 (하루 3개 제한 방지)
            diaryRepository.findByAuthorEmail("test@example.com").forEach(d -> diaryRepository.delete(d));

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

    @Test
    public void testFullTokenFlow() throws Exception {
        System.out.println("전체 토큰 흐름 테스트 시작...");
        String testEmail = "tokenflow@example.com";
        String testPassword = "password123";
        String nickname = "테스터";

        try {
            // 1. 기존 유저 및 일기 정리
            userRepository.findByUserEmail(testEmail).ifPresent(u -> userRepository.delete(u));
            diaryRepository.findByAuthorEmail(testEmail).forEach(d -> diaryRepository.delete(d));
            System.out.println("1. 이전 데이터 정리 완료");

            // 2. 회원가입
            com.jaehyun.diary.dto.AuthForm.RegisterForm regForm = new com.jaehyun.diary.dto.AuthForm.RegisterForm();
            regForm.setUserEmail(testEmail);
            regForm.setRawPassword(testPassword);
            regForm.setUserNickname(nickname);
            authService.register(regForm);
            System.out.println("2. 회원가입 완료");

            // 3. 로그인 및 토큰 발급
            com.jaehyun.diary.dto.AuthForm.LoginForm loginForm = new com.jaehyun.diary.dto.AuthForm.LoginForm();
            loginForm.setUserEmail(testEmail);
            loginForm.setRawPassword(testPassword);
            com.jaehyun.diary.dto.AuthForm.TokenResponse tokenRes = authService.login(loginForm);
            String token = tokenRes.getAccessToken();
            System.out.println("2. 로그인 성공, 토큰 획득: " + token);

            // 4. JWT 토큰을 실어 일기 작성 API 호출 (이미지 첨부 포함)
            String diaryJson = "{\"diaryTitle\":\"토큰 테스트 제목\",\"diaryContent\":\"토큰 테스트 내용\",\"writtenDate\":\"2026-06-15\"}";
            org.springframework.mock.web.MockMultipartFile diaryPart = new org.springframework.mock.web.MockMultipartFile(
                    "diary",
                    "",
                    "application/json",
                    diaryJson.getBytes()
            );

            org.springframework.mock.web.MockMultipartFile imagePart = new org.springframework.mock.web.MockMultipartFile(
                    "image",
                    "upload-test.png",
                    "image/png",
                    "fake image data".getBytes()
            );

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/diary")
                    .file(diaryPart)
                    .file(imagePart)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
            System.out.println("3. JWT 인증 기반 일기 작성 및 S3 업로드 API 성공!");

            // 5. 일기 목록 조회
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/diary")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
            System.out.println("4. JWT 인증 기반 일기 목록 조회 API 성공!");

        } catch (Exception e) {
            System.err.println("전체 토큰 흐름 테스트 실패!");
            e.printStackTrace();
            throw e;
        }
    }
}
