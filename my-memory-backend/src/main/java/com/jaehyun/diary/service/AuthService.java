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

    public void register(AuthForm.RegisterForm registrationRequest) {
        if (userRepository.existsByUserEmail(registrationRequest.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserEntity user = UserEntity.builder()
                .userEmail(registrationRequest.getUserEmail())
                .encryptedPassword(passwordEncoder.encode(registrationRequest.getRawPassword()))
                .userNickname(registrationRequest.getUserNickname())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public AuthForm.TokenResponse login(AuthForm.LoginForm loginRequest) {
        UserEntity user = userRepository.findByUserEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getRawPassword(), user.getEncryptedPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String token = jwtUtil.createToken(user.getUserEmail(), user.getUserRole().name());
        
        return new AuthForm.TokenResponse(token, user.getUserEmail(), user.getUserNickname());
    }
}
