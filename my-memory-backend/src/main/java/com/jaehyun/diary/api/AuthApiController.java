package com.jaehyun.diary.api;

import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 사용자 인증 및 권한 부여 REST API 컨트롤러
// 클라이언트(프론트엔드 등)로부터 회원가입 및 로그인 HTTP 요청을 받아 AuthService로 위임, HTTP 응답으로 반환
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    // authService
    @Autowired
    private AuthService authService;

    // 신규 사용자 계정 생성 API
    // param: registrationRequest - 클라이언트가 전송한 회원가입 폼 데이터 (이메일, 비밀번호 등)가 매핑되는 DTO 객체
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthForm.RegisterForm registrationRequest) {
        // authService를 호출
        authService.register(registrationRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 사용자 로그인 처리 및 JWT 토큰 반환 API
    // param: loginRequest - 클라이언트가 전송한 로그인 폼 데이터 (이메일, 비밀번호)가 매핑되는 DTO 객체
    // return: HTTP 상태 코드 200(OK)과 발급된 JWT 토큰 및 유저 정보를 담은 TokenResponse DTO 객체
    @PostMapping("/login")
    public ResponseEntity<AuthForm.TokenResponse> login(@RequestBody AuthForm.LoginForm loginRequest) {
        // authService를 호출하여 로그인 검증을 수행하고, 성공 시 JWT 토큰이 포함된 응답 데이터(response)를 받아옴
        AuthForm.TokenResponse response = authService.login(loginRequest);
        // 정상 처리 시 발급된 토큰 정보를 JSON 형태로 클라이언트에게 반환
        return ResponseEntity.ok(response);
    }
}
