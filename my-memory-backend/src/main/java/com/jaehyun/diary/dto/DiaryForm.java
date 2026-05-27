package com.jaehyun.diary.dto;

import com.jaehyun.diary.entity.DiaryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryForm {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String emotion;
    private String imageUrl;
    private LocalDate createdAt;

    public DiaryEntity toEntity() {
        DiaryEntity entity = new DiaryEntity();
        entity.setId(this.id);
        entity.setUserId(this.userId);
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setEmotion(this.emotion);
        entity.setImageUrl(this.imageUrl);
        entity.setCreatedAt(this.createdAt == null ? LocalDate.now() : this.createdAt);
        return entity;
    }

    public static DiaryForm fromEntity(DiaryEntity entity) {
        if (entity == null) return null;
        return new DiaryForm(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getEmotion(),
                entity.getImageUrl(),
                entity.getCreatedAt()
        );
    }
}
