package com.springsecurity.example2.controller;

import com.springsecurity.example2.config.JwtUtil;
import com.springsecurity.example2.dto.JoinDTO;
import com.springsecurity.example2.dto.LoginDTO;
import com.springsecurity.example2.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final JoinService joinService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinDTO joinDTO) {
        joinService.joinProcess(joinDTO);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 (JWT 발급)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = joinService.login(loginDTO.getLoginId(), loginDTO.getPassword());
        return ResponseEntity.ok().body(token);
    }
}
