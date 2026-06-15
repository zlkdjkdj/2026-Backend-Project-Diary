package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.DiaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

// 일기 Repository
public interface DiaryRepository extends MongoRepository<DiaryEntity, String> {

    List<DiaryEntity> findByAuthorEmail(String authorEmail);

    List<DiaryEntity> findByAuthorEmailAndDiaryContentContaining(String authorEmail, String diaryContent);

    int countByAuthorEmailAndWrittenDate(String authorEmail, LocalDate writtenDate);
}
