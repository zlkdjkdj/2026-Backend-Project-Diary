package com.jaehyun.diary.service;

import com.jaehyun.diary.config.JwtUtil;
import com.jaehyun.diary.dto.AuthForm;
import com.jaehyun.diary.entity.UserEntity;
import com.jaehyun.diary.entity.UserRole;
import com.jaehyun.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 회원가입, 로그인 등 사용자의 보안 관련 핵심 비즈니스 로직을 처리하는 객체
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // JWT 생성 및 검증 util
    @Autowired
    private JwtUtil jwtUtil;

    // 신규 사용자 계정 등록 (이메일 중복 확인 및 비밀번호 해싱)
    public void register(AuthForm.RegisterForm registrationRequest) {
        // 중복 가입을 방지
        if (userRepository.existsByUserEmail(registrationRequest.getUserEmail())) {
            // 중복 시 예외를 발생시켜 가입 절차 중단
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // DTO에서 추출한 정보로 새로운 UserEntity(DB에 저장될 객체)를 조립
        UserEntity user = UserEntity.builder()
                .userEmail(registrationRequest.getUserEmail())
                // 비밀번호 암호호 세팅
                .encryptedPassword(passwordEncoder.encode(registrationRequest.getRawPassword()))
                .userNickname(registrationRequest.getUserNickname())
                // 회원가입 시 기본 권한은 USER
                .userRole(UserRole.USER)
                .build();

        // 회원가입 완료
        userRepository.save(user);
    }

    // 사용자 인증 수행 및 JWT 토큰 발급
    // loginRequest - 로그인 폼 객체
    public AuthForm.TokenResponse login(AuthForm.LoginForm loginRequest) {
        // DB에서 입력된 이메일로 사용자 엔티티를 조회, 이메일 없으면 미가입 
        UserEntity user = userRepository.findByUserEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 평문 비번과 암호화 비번 일치 확인
        if (!passwordEncoder.matches(loginRequest.getRawPassword(), user.getEncryptedPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 인증을 통과한 사용자에게 부여하는 JWT 문자열, 이메일과 권한(Role) 정보를 담아 생성
        String token = jwtUtil.createToken(user.getUserEmail(), user.getUserRole().name());

        // 최종적으로 클라이언트가 사용할 수 있도록 토큰과 기본 정보를 DTO에 담아 반환
        return new AuthForm.TokenResponse(token, user.getUserEmail(), user.getUserNickname());
    }
}
