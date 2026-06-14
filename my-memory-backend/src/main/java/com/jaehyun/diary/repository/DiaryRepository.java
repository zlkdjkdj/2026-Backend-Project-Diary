package com.jaehyun.diary.repository;

import com.jaehyun.diary.entity.DiaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

// 일기(diary) 컬렉션 데이터 접근 리포지토리 인터페이스
public interface DiaryRepository extends MongoRepository<DiaryEntity, String> {

    // 특정 작성자 전체 일기 레코드 조회
    List<DiaryEntity> findByAuthorEmail(String authorEmail);

    // 특정 작성자 일기 중 지정 키워드 포함 레코드 검색
    List<DiaryEntity> findByAuthorEmailAndDiaryContentContaining(String authorEmail, String diaryContent);

    // 특정 작성자의 지정 날짜 일기 작성 총 개수 산출
    int countByAuthorEmailAndWrittenDate(String authorEmail, LocalDate writtenDate);
}
