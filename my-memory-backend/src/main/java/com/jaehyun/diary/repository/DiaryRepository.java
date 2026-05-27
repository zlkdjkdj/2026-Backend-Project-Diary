package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.Diary;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DiaryRepository extends MongoRepository<Diary, String> {
    List<Diary> findByUserIdAndContentContaining(String userId, String content);
}
