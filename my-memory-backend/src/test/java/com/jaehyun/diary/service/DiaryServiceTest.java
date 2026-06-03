package com.jaehyun.diary.service;

import com.jaehyun.diary.dto.DiaryForm;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @MockitoBean
    private DiaryRepository diaryRepository;

    @Test
    void updateDiary_success_whenOwnerMatches() {
        // Given
        String email = "owner@test.com";
        String diaryId = "diary123";
        
        DiaryEntity existingDiary = new DiaryEntity();
        existingDiary.setId(diaryId);
        existingDiary.setUserId(email);
        existingDiary.setTitle("Old Title");
        existingDiary.setContent("Old Content");
        existingDiary.setCreatedAt(LocalDate.now());

        DiaryForm updateForm = new DiaryForm();
        updateForm.setTitle("New Title");
        updateForm.setContent("New Content");

        DiaryEntity updatedEntity = new DiaryEntity();
        updatedEntity.setId(diaryId);
        updatedEntity.setUserId(email);
        updatedEntity.setTitle("New Title");
        updatedEntity.setContent("New Content");
        updatedEntity.setCreatedAt(LocalDate.now());

        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(existingDiary));
        when(diaryRepository.save(any(DiaryEntity.class))).thenReturn(updatedEntity);

        // When
        DiaryForm result = diaryService.updateDiary(email, diaryId, updateForm);

        // Then
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(diaryRepository, times(1)).findById(diaryId);
        verify(diaryRepository, times(1)).save(any(DiaryEntity.class));
    }

    @Test
    void updateDiary_throwsException_whenOwnerMismatches() {
        // Given
        String email = "other@test.com";
        String diaryId = "diary123";
        
        DiaryEntity existingDiary = new DiaryEntity();
        existingDiary.setId(diaryId);
        existingDiary.setUserId("owner@test.com"); // mismatch
        existingDiary.setTitle("Old Title");
        existingDiary.setContent("Old Content");

        DiaryForm updateForm = new DiaryForm();
        updateForm.setTitle("New Title");
        updateForm.setContent("New Content");

        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(existingDiary));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            diaryService.updateDiary(email, diaryId, updateForm);
        });

        assertEquals("권한이 없습니다.", exception.getMessage());
        verify(diaryRepository, times(1)).findById(diaryId);
        verify(diaryRepository, never()).save(any(DiaryEntity.class));
    }

    @Test
    void deleteDiary_success_whenOwnerMatches() {
        // Given
        String email = "owner@test.com";
        String diaryId = "diary123";
        
        DiaryEntity existingDiary = new DiaryEntity();
        existingDiary.setId(diaryId);
        existingDiary.setUserId(email);

        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(existingDiary));
        doNothing().when(diaryRepository).deleteById(diaryId);

        // When
        diaryService.deleteDiary(email, diaryId);

        // Then
        verify(diaryRepository, times(1)).findById(diaryId);
        verify(diaryRepository, times(1)).deleteById(diaryId);
    }
}
