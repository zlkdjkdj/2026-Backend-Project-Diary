package com.jaehyun.diary.api;

import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthForm.RegisterForm request) {
        authService.register(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthForm.TokenResponse> login(@RequestBody AuthForm.LoginForm request) {
        AuthForm.TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthForm.TokenResponse> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String refreshToken = authorizationHeader.substring(7);
        AuthForm.TokenResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}
