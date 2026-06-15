package com.jaehyun.diary.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

// MongoDB 'diary' 컬렉션 매핑 일기 영속성 엔티티
// 일기의 메타데이터와 본문, 첨부파일 경로를 구조화하여 DB에 저장하는 객체
@Data // Getter, Setter 등 유틸리티 메서드 자동 생성(Lombok)
@Document(collection = "diary") // MongoDB의 diary 컬렉션과 매핑
public class DiaryEntity {
    
    // MongoDB 문서 고유 식별자 (Object ID)
    @Id
    private String diaryId;
    
    // 일기 작성자 고유 이메일 계정 ->CheckOwnership 사용
    private String authorEmail;
    
    // 일기 제목
    private String diaryTitle;
    
    // 일기 본문 
    private String diaryContent;
    
    // 서버 내 로컬 스토리지에 저장된 이미지의 웹 접근 경로 문자열
    private String attachedPhotoUrl;
    
    // 일기 작성 일자
    private LocalDate writtenDate;
}
