package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// 사용자(users) 컬렉션 데이터 접근 리포지토리 인터페이스
public interface UserRepository extends MongoRepository<UserEntity, String> {

    // 고유 사용자 이메일 기준 엔티티 조회
    Optional<UserEntity> findByUserEmail(String userEmail);

    // 특정 이메일 계정 데이터베이스 존재 여부 확인
    boolean existsByUserEmail(String userEmail);
}
