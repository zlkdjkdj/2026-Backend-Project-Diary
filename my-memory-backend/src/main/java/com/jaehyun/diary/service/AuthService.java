package com.jaehyun.diary.service;

import com.jaehyun.diary.config.JwtUtil;
import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.entity.UserEntity;
import com.jaehyun.diary.entity.UserRole;
import com.jaehyun.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 인증 서비스
@Service
@SuppressWarnings("null")
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 회원가입
    public void register(AuthForm.RegisterForm registrationRequest) {
        if (userRepository.existsByUserEmail(registrationRequest.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserEntity user = UserEntity.builder()
                .userEmail(registrationRequest.getUserEmail())
                // 평문 비밀번호를 단방향 암호화(BCrypt) 처리 후 저장
                .encryptedPassword(passwordEncoder.encode(registrationRequest.getRawPassword()))
                .userNickname(registrationRequest.getUserNickname())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    // 로그인
    public AuthForm.TokenResponse login(AuthForm.LoginForm loginRequest) {
        UserEntity user = userRepository.findByUserEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 평문 비밀번호와 BCrypt 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(loginRequest.getRawPassword(), user.getEncryptedPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 인증 성공 시 사용자 이메일과 권한으로 JWT 토큰 발급
        String token = jwtUtil.createToken(user.getUserEmail(), user.getUserRole().name());

        return new AuthForm.TokenResponse(token, user.getUserEmail(), user.getUserNickname());
    }
}
