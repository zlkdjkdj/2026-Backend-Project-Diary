package com.springsecurity.example1.service;

import com.springsecurity.example1.dto.JoinDTO;
import com.springsecurity.example1.entity.User;
import com.springsecurity.example1.entity.UserRole;
import com.springsecurity.example1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // SecurityConfig에서 등록한 Bean 주입

    @Transactional
    public void joinProcess(JoinDTO joinDTO) {

        // 1. 중복 ID 검증 (실무에선 예외 처리를 더 디테일하게 합니다)
        if (userRepository.existsByLoginId(joinDTO.getLoginId())) {
            return; // 이미 존재하면 강제 종료 (원래는 에러를 던져서 알림을 줘야 함)
        }

        // 2. 비밀번호 암호화 (필수!)
        // 시큐리티로 로그인을 하려면 비밀번호가 암호화 되어있어야 합니다.
        String rawPassword = joinDTO.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        // 3. 권한 로직 처리
        // DTO에서 "ADMIN"이라는 문자열이 넘어오면 ADMIN 권한 부여, 아니면 USER
        UserRole role = UserRole.USER;
        if ("ADMIN".equals(joinDTO.getRole())) {
            role = UserRole.ADMIN;
        }
        // 3. 엔티티 생성 및 저장
        User user = User.builder()
                .loginId(joinDTO.getLoginId())
                .password(encPassword) // 암호화된 비번 저장
                .nickname(joinDTO.getNickname())
                .role(role)
                .build();

        userRepository.save(user);
    }
}