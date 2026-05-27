package com.springsecurity.example1.service;

import com.springsecurity.example1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    @Transactional
//    public void join(JoinDTO req) {
//        // 1. 중복 체크 (Repository 활용)
//        if(userRepository.existsByLoginId(req.getLoginId())) {
//            throw new RuntimeException("이미 존재하는 아이디입니다.");
//        }
//
//        // 2. 저장
//        User user = User.builder()
//                .loginId(req.getLoginId())
//                .password(req.getPassword()) // 실제론 암호화 필요
//                .nickname(req.getNickname())
//                .role(UserRole.USER)
//                .build();
//
//        userRepository.save(user);
//    }

//    /**아이디 중복 검사 (컨트롤러에서 호출용) */
//    public boolean checkIdDuplicate(String loginId) {
//        return userRepository.existsByLoginId(loginId);
//    }

//    /** 로그인 로직   */
//    public User login(String loginId, String password) {
//        // 1. 아이디로 조회 (Optional 활용)
//        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
//
//        // 2. 아이디가 없으면 null 반환 (또는 예외 발생)
//        if(optionalUser.isEmpty()) {
//            return null;
//        }
//
//        User user = optionalUser.get();
//
//        // 3. 비밀번호 일치 확인
//        if(!user.getPassword().equals(password)) {
//            return null;
//        }
//
//        return user;
//    }
}