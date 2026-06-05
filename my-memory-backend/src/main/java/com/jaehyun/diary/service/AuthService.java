package com.jaehyun.diary.service;

import com.jaehyun.diary.config.JwtUtil;
import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.entity.UserEntity;
import com.jaehyun.diary.entity.UserRole;
import com.jaehyun.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public void register(AuthForm.RegisterForm request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public AuthForm.TokenResponse login(AuthForm.LoginForm request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String token = jwtUtil.createToken(user.getEmail(), user.getRole().name());
        
        return new AuthForm.TokenResponse(token, user.getEmail(), user.getNickname());
    }
}
