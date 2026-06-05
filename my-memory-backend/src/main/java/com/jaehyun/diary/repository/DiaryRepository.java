package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.DiaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DiaryRepository extends MongoRepository<DiaryEntity, String> {
    List<DiaryEntity> findByUserId(String userId);
    List<DiaryEntity> findByUserIdAndContentContaining(String userId, String content);
    int countByUserIdAndCreatedAt(String userId, java.time.LocalDate createdAt);
}
