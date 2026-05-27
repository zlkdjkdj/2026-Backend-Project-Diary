package com.jaehyun.diary.service;

import com.jaehyun.diary.dto.DiaryDto;
import com.jaehyun.diary.entity.Diary;
import com.jaehyun.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryDto createDiary(DiaryDto diaryDto) {
        Diary savedDiary = diaryRepository.save(diaryDto.toEntity());
        return DiaryDto.fromEntity(savedDiary);
    }

    public List<DiaryDto> getAllDiaries() {
        return diaryRepository.findAll().stream()
                .map(DiaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DiaryDto> searchDiaries(String userId, String keyword) {
        return diaryRepository.findByUserIdAndContentContaining(userId, keyword).stream()
                .map(DiaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DiaryDto updateDiary(String id, DiaryDto diaryDto) {
        if (!diaryRepository.existsById(id)) {
            throw new IllegalArgumentException("Diary with id " + id + " does not exist.");
        }
        Diary diary = diaryDto.toEntity();
        diary.setId(id);
        Diary updatedDiary = diaryRepository.save(diary);
        return DiaryDto.fromEntity(updatedDiary);
    }

    public void deleteDiary(String id) {
        if (!diaryRepository.existsById(id)) {
            throw new IllegalArgumentException("Diary with id " + id + " does not exist.");
        }
        diaryRepository.deleteById(id);
    }
}
