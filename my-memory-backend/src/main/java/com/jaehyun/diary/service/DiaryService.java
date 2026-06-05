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

    public DiaryForm createDiary(String email, DiaryForm form) {
        java.time.LocalDate today = form.getCreatedAt() != null ? form.getCreatedAt() : java.time.LocalDate.now();
        int todayCount = diaryRepository.countByUserIdAndCreatedAt(email, today);
        if (todayCount >= 3) {
            throw new RuntimeException("하루에 작성할 수 있는 일기는 최대 3개입니다.");
        }
        
        form.setUserId(email);
        DiaryEntity savedDiary = diaryRepository.save(form.toEntity());
        return DiaryForm.fromEntity(savedDiary);
    }

    public List<DiaryForm> getAllDiaries(String email) {
        return diaryRepository.findByUserId(email).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DiaryForm> searchDiaries(String email, String keyword) {
        return diaryRepository.findByUserIdAndContentContaining(email, keyword).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    @CheckOwnership
    public DiaryForm updateDiary(String email, String id, DiaryForm form) {
        form.setUserId(email);
        DiaryEntity updatedDiary = form.toEntity();
        updatedDiary.setId(id);
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    @CheckOwnership
    public void deleteDiary(String email, String id) {
        diaryRepository.deleteById(id);
    }
}
