package com.jaehyun.diary.service;

import com.jaehyun.diary.config.CheckOwnership;
import com.jaehyun.diary.dto.DiaryForm;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

// 일기 서비스
@Service
@SuppressWarnings("null")
public class DiaryService {
    @Autowired
    private DiaryRepository diaryRepository;

    // 일기 생성
    public DiaryForm createDiary(String email, DiaryForm diaryInputData) {
        LocalDate today = diaryInputData.getWrittenDate() != null ? diaryInputData.getWrittenDate() : LocalDate.now();
        int todayCount = diaryRepository.countByAuthorEmailAndWrittenDate(email, today);

        // 하루 일기 개수 제한
        // 비즈니스 룰: 하루 작성 가능한 일기 개수를 최대 3개로 제한
        if (todayCount >= 3) {
            throw new RuntimeException("하루에 작성할 수 있는 일기는 최대 3개입니다.");
        }

        diaryInputData.setAuthorEmail(email);
        DiaryEntity savedDiary = diaryRepository.save(diaryInputData.toEntity());
        return DiaryForm.fromEntity(savedDiary);
    }

    // 전체 일기 조회
    public List<DiaryForm> getAllDiaries(String email) {
        return diaryRepository.findByAuthorEmail(email).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    // 일기 검색
    public List<DiaryForm> searchDiaries(String email, String searchKeyword) {
        return diaryRepository.findByAuthorEmailAndDiaryContentContaining(email, searchKeyword).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    // 일기 수정
    @CheckOwnership
    public DiaryForm updateDiary(String email, String id, DiaryForm diaryInputData) {
        diaryInputData.setAuthorEmail(email);
        DiaryEntity updatedDiary = diaryInputData.toEntity();
        // 기존 일기의 ID를 명시적으로 세팅하여 저장 시 수정(Update) 처리 유도
        updatedDiary.setDiaryId(id);
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    // 일기 삭제
    @CheckOwnership
    public void deleteDiary(String email, String id) {
        diaryRepository.deleteById(id);
    }
}
