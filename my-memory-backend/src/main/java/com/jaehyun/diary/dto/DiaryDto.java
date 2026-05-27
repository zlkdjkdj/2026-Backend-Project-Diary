package com.jaehyun.diary.dto;

import com.jaehyun.diary.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {

    private String id;
    private String userId;
    private String title;
    private String content;
    private String emotion;
    private String imageUrl;
    private LocalDate createdAt;

    // Entity로 변환하는 메서드
    public Diary toEntity() {
        Diary diary = new Diary();
        diary.setId(this.id);
        diary.setUserId(this.userId);
        diary.setTitle(this.title);
        diary.setContent(this.content);
        diary.setEmotion(this.emotion);
        diary.setImageUrl(this.imageUrl);
        // 생성일이 없을 경우 현재 날짜를 설정하거나 그대로 둠.
        diary.setCreatedAt(this.createdAt == null ? LocalDate.now() : this.createdAt);
        return diary;
    }

    // Entity를 DTO로 변환하는 정적 메서드
    public static DiaryDto fromEntity(Diary diary) {
        if (diary == null) return null;
        return new DiaryDto(
                diary.getId(),
                diary.getUserId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getEmotion(),
                diary.getImageUrl(),
                diary.getCreatedAt()
        );
    }
}
