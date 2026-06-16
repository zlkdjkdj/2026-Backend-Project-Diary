package com.jaehyun.diary.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

// 일기 정보 엔티티
@Data
@Document(collection = "diary")
public class DiaryEntity {
    
    @Id
    private String diaryId;
    
    private String authorEmail;
    
    private String diaryTitle;
    
    private String diaryContent;
    
    private String attachedPhotoUrl;
    
    private LocalDate writtenDate;
}
