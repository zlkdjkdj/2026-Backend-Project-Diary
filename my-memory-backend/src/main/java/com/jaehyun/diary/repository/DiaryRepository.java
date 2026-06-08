package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.DiaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DiaryRepository extends MongoRepository<DiaryEntity, String> {
    List<DiaryEntity> findByAuthorEmail(String authorEmail);
    List<DiaryEntity> findByAuthorEmailAndDiaryContentContaining(String authorEmail, String diaryContent);
    int countByAuthorEmailAndWrittenDate(String authorEmail, java.time.LocalDate writtenDate);
}
