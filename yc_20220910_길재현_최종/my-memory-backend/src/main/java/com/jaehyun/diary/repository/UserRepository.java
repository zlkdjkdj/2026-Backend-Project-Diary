package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// 사용자 DB 리포지토리
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);
}
