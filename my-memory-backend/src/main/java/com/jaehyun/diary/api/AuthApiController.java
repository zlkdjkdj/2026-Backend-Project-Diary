package com.jaehyun.diary.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.service.AuthService;

// 사용자 인증 및 권한 부여 REST API 컨트롤러
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    // authService
    @Autowired
    private AuthService authService;

    // 회원가입 api
    // registrationRequest =  dto 객체 
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthForm.RegisterForm registrationRequest) {
        // authService를 호출
        authService.register(registrationRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 사용자 로그인 처리 및 JWT 토큰 반환 API
    // loginRequest =  DTO 객체
    @PostMapping("/login")
    public ResponseEntity<AuthForm.TokenResponse> login(@RequestBody AuthForm.LoginForm loginRequest) {
        // 로그인 검증 수행 + JWT 토큰 정보 수령
        AuthForm.TokenResponse response = authService.login(loginRequest);
        // 정상 처리 시 발급된 토큰 정보를 JSON 형태로 클라이언트에게 반환
        return ResponseEntity.ok(response);
    }
}
