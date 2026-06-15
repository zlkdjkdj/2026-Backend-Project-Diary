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

// 인증 API 컨트롤러
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthForm.RegisterForm registrationRequest) {
        authService.register(registrationRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthForm.TokenResponse> login(@RequestBody AuthForm.LoginForm loginRequest) {
        AuthForm.TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
