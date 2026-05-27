package com.jaehyun.diary.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "diary")
public class DiaryEntity {
    @Id
    private String id;
    private String userId;
    private String title;
    private String content;
    private String emotion;
    private String imageUrl;
    private LocalDate createdAt;
}
