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

    // JSON Web Token을 생성(발급)하거나 검증하는 역할을 전담하는 커스텀 유틸리티 객체
    @Autowired
    private JwtUtil jwtUtil;

    // 신규 사용자 계정 등록 (이메일 중복 확인 및 비밀번호 해싱)
    // registrationRequest - 클라이언트가 전송한 회원가입 폼 데이터 (이메일, 평문 비밀번호, 닉네임 등) 객체
    public void register(AuthForm.RegisterForm registrationRequest) {
        // DB에 해당 이메일이 이미 존재하는지 검사하여 중복 가입을 방지
        if (userRepository.existsByUserEmail(registrationRequest.getUserEmail())) {
            // 중복 시 예외를 발생시켜 가입 절차 중단
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // user: 빌더 패턴을 사용하여 DTO에서 추출한 정보로 새로운 UserEntity(DB에 저장될 객체)를 조립
        UserEntity user = UserEntity.builder()
                .userEmail(registrationRequest.getUserEmail())
                // 비밀번호는 반드시 passwordEncoder를 거쳐 암호화된 상태로 엔티티에 세팅
                .encryptedPassword(passwordEncoder.encode(registrationRequest.getRawPassword()))
                .userNickname(registrationRequest.getUserNickname())
                // 회원가입 시 기본 권한은 USER로 강제 할당
                .userRole(UserRole.USER)
                .build();

        // DB에 저장하여 회원가입 완료
        userRepository.save(user);
    }

    // 사용자 인증 수행 및 JWT 토큰 발급
    // loginRequest - 클라이언트가 전송한 로그인 폼 데이터 (이메일, 평문 비밀번호) 객체
    public AuthForm.TokenResponse login(AuthForm.LoginForm loginRequest) {
        // user: DB에서 입력된 이메일로 사용자 엔티티를 조회. 결과가 없으면 예외(가입되지 않은 이메일) 발생
        UserEntity user = userRepository.findByUserEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // passwordEncoder.matches(): 클라이언트가 보낸 평문 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하는지 비교 검증
        if (!passwordEncoder.matches(loginRequest.getRawPassword(), user.getEncryptedPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // token: 인증을 통과한 사용자에게 부여할 JWT 문자열. 내부 Payload에 이메일과 권한(Role) 정보를 담아 생성
        String token = jwtUtil.createToken(user.getUserEmail(), user.getUserRole().name());

        // 최종적으로 클라이언트가 사용할 수 있도록 토큰과 기본 정보를 DTO에 담아 반환
        return new AuthForm.TokenResponse(token, user.getUserEmail(), user.getUserNickname());
    }
}
