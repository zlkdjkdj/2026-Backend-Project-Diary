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
@SuppressWarnings("null")
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
        existingDiary.setDiaryId(diaryId);
        existingDiary.setAuthorEmail(email);
        existingDiary.setDiaryTitle("Old Title");
        existingDiary.setDiaryContent("Old Content");
        existingDiary.setWrittenDate(LocalDate.now());

        DiaryForm updateForm = new DiaryForm();
        updateForm.setDiaryTitle("New Title");
        updateForm.setDiaryContent("New Content");

        DiaryEntity updatedEntity = new DiaryEntity();
        updatedEntity.setDiaryId(diaryId);
        updatedEntity.setAuthorEmail(email);
        updatedEntity.setDiaryTitle("New Title");
        updatedEntity.setDiaryContent("New Content");
        updatedEntity.setWrittenDate(LocalDate.now());

        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(existingDiary));
        when(diaryRepository.save(any(DiaryEntity.class))).thenReturn(updatedEntity);

        // When
        DiaryForm result = diaryService.updateDiary(email, diaryId, updateForm);

        // Then
        assertNotNull(result);
        assertEquals("New Title", result.getDiaryTitle());
        assertEquals("New Content", result.getDiaryContent());
        verify(diaryRepository, times(1)).findById(diaryId);
        verify(diaryRepository, times(1)).save(any(DiaryEntity.class));
    }

    @Test
    void updateDiary_throwsException_whenOwnerMismatches() {
        // Given
        String email = "other@test.com";
        String diaryId = "diary123";
        
        DiaryEntity existingDiary = new DiaryEntity();
        existingDiary.setDiaryId(diaryId);
        existingDiary.setAuthorEmail("owner@test.com"); // mismatch
        existingDiary.setDiaryTitle("Old Title");
        existingDiary.setDiaryContent("Old Content");

        DiaryForm updateForm = new DiaryForm();
        updateForm.setDiaryTitle("New Title");
        updateForm.setDiaryContent("New Content");

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
        existingDiary.setDiaryId(diaryId);
        existingDiary.setAuthorEmail(email);

        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(existingDiary));
        doNothing().when(diaryRepository).deleteById(diaryId);

        // When
        diaryService.deleteDiary(email, diaryId);

        // Then
        verify(diaryRepository, times(1)).findById(diaryId);
        verify(diaryRepository, times(1)).deleteById(diaryId);
    }
}
