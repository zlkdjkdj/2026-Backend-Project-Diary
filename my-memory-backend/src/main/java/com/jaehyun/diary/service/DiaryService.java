package com.jaehyun.diary.service;

import com.jaehyun.diary.config.CheckOwnership;
import com.jaehyun.diary.dto.DiaryForm;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    public DiaryForm createDiary(String email, DiaryForm diaryInputData) {
        java.time.LocalDate today = diaryInputData.getWrittenDate() != null ? diaryInputData.getWrittenDate() : java.time.LocalDate.now();
        int todayCount = diaryRepository.countByAuthorEmailAndWrittenDate(email, today);
        if (todayCount >= 3) {
            throw new RuntimeException("하루에 작성할 수 있는 일기는 최대 3개입니다.");
        }
        
        diaryInputData.setAuthorEmail(email);
        DiaryEntity savedDiary = diaryRepository.save(diaryInputData.toEntity());
        return DiaryForm.fromEntity(savedDiary);
    }

    public List<DiaryForm> getAllDiaries(String email) {
        return diaryRepository.findByAuthorEmail(email).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DiaryForm> searchDiaries(String email, String searchKeyword) {
        return diaryRepository.findByAuthorEmailAndDiaryContentContaining(email, searchKeyword).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    @CheckOwnership
    public DiaryForm updateDiary(String email, String id, DiaryForm diaryInputData) {
        diaryInputData.setAuthorEmail(email);
        DiaryEntity updatedDiary = diaryInputData.toEntity();
        updatedDiary.setDiaryId(id);
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    @CheckOwnership
    public void deleteDiary(String email, String id) {
        diaryRepository.deleteById(id);
    }
}
