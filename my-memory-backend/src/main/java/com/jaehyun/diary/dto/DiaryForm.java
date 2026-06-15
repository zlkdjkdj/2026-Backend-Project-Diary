package com.jaehyun.diary.dto;

import com.jaehyun.diary.entity.DiaryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 일기 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryForm {
    private String diaryId;
    private String authorEmail;
    private String diaryTitle;
    private String diaryContent;
    private String attachedPhotoUrl;
    private LocalDate writtenDate;

    // Entity로 변환
    public DiaryEntity toEntity() {
        DiaryEntity entity = new DiaryEntity();
        entity.setDiaryId(this.diaryId);
        entity.setAuthorEmail(this.authorEmail);
        entity.setDiaryTitle(this.diaryTitle);
        entity.setDiaryContent(this.diaryContent);
        entity.setAttachedPhotoUrl(this.attachedPhotoUrl);
        entity.setWrittenDate(this.writtenDate == null ? LocalDate.now() : this.writtenDate);
        return entity;
    }

    // Entity로부터 DTO 생성
    public static DiaryForm fromEntity(DiaryEntity entity) {
        if (entity == null)
            return null;
        return new DiaryForm(
                entity.getDiaryId(),
                entity.getAuthorEmail(),
                entity.getDiaryTitle(),
                entity.getDiaryContent(),
                entity.getAttachedPhotoUrl(),
                entity.getWrittenDate());
    }
}
