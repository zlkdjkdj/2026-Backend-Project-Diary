package com.jaehyun.diary.service;

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

    public DiaryForm updateDiary(String email, String id, DiaryForm form) {
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));
        if (!diary.getUserId().equals(email)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        form.setUserId(email);
        DiaryEntity updatedDiary = form.toEntity();
        updatedDiary.setId(id);
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    public void deleteDiary(String email, String id) {
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));
        if (!diary.getUserId().equals(email)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        diaryRepository.deleteById(id);
    }
}
