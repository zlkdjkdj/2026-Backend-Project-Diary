package com.jaehyun.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 클라이언트-서버 간 인증/계정 관련 데이터 전송 객체(DTO) 컨테이너
public class AuthForm {

    // 클라이언트 로그인 요청 데이터 컨테이너
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {
        // 입력받은 이메일
        private String userEmail;
        // 입력받은 평문 비밀번호 (보안 처리 전)
        private String rawPassword;
    }

    // 클라이언트 신규 사용자 가입 요청 데이터 컨테이너
    // 역할: 회원가입 시 입력하는 정보들을 담아 컨트롤러로 전달하는 객체
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterForm {
        // 등록할 이메일 아이디
        private String userEmail;
        // 등록할 평문 비밀번호
        private String rawPassword;
        // 사용할 닉네임
        private String userNickname;
    }

    // 인증 성공 후 클라이언트 반환용 토큰 및 기본 정보 컨테이너
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        // 발급된 JWT 문자열
        private String accessToken;
        // 사용자 계정
        private String userEmail;
        // 화면 표시용 닉네임
        private String userNickname;
    }
}
