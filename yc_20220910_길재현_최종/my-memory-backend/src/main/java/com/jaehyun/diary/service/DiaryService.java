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

    // 생성 로직
    public DiaryForm createDiary(String email, DiaryForm diaryInputData) {
        LocalDate today = diaryInputData.getWrittenDate() != null ? diaryInputData.getWrittenDate() : LocalDate.now();
        int todayCount = diaryRepository.countByAuthorEmailAndWrittenDate(email, today);

        // 하루 작성 최대 3개 제한
        if (todayCount >= 3) {
            throw new RuntimeException("하루에 작성할 수 있는 일기는 최대 3개입니다.");
        }

        diaryInputData.setAuthorEmail(email);
        DiaryEntity savedDiary = diaryRepository.save(diaryInputData.toEntity());
        return DiaryForm.fromEntity(savedDiary);
    }

    // 전체 조회 로직
    public List<DiaryForm> getAllDiaries(String email) {
        return diaryRepository.findByAuthorEmail(email).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    // 검색 로직
    public List<DiaryForm> searchDiaries(String email, String searchKeyword) {
        return diaryRepository.findByAuthorEmailAndDiaryContentContaining(email, searchKeyword).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    // 수정 로직
    @CheckOwnership
    public DiaryForm updateDiary(String email, String id, DiaryForm diaryInputData) {
        diaryInputData.setAuthorEmail(email);
        DiaryEntity updatedDiary = diaryInputData.toEntity();
        // ID 세팅 후 저장
        updatedDiary.setDiaryId(id);
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    // 삭제 로직
    @CheckOwnership // 본인 검증
    public void deleteDiary(String email, String id) {
        diaryRepository.deleteById(id); // db에서 삭제 처리
    }
}
