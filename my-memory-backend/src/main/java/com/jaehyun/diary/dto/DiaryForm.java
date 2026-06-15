package com.jaehyun.diary.dto;

import com.jaehyun.diary.entity.DiaryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 클라이언트-애플리케이션 간 일기 데이터 전송 객체(DTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryForm {
    // 일기 고유 ID
    private String diaryId;
    // 작성자 이메일
    private String authorEmail;
    // 제목
    private String diaryTitle;
    // 내용
    private String diaryContent;
    // 첨부 이미지 경로
    private String attachedPhotoUrl;
    // 작성일
    private LocalDate writtenDate;

    // DTO 데이터를 영속성 엔티티(DiaryEntity)로 변환
    public DiaryEntity toEntity() {
        DiaryEntity entity = new DiaryEntity();
        // 각 필드를 엔티티에 매핑
        entity.setDiaryId(this.diaryId);
        entity.setAuthorEmail(this.authorEmail);
        entity.setDiaryTitle(this.diaryTitle);
        entity.setDiaryContent(this.diaryContent);
        entity.setAttachedPhotoUrl(this.attachedPhotoUrl);
        // 클라이언트가 작성일자를 넘기지 않았으면 현재 서버의 날짜를 기준일로 자동 할당
        entity.setWrittenDate(this.writtenDate == null ? LocalDate.now() : this.writtenDate);
        return entity;
    }

    // 영속성 엔티티(DiaryEntity)를 클라이언트 전송용 DTO로 변환
    public static DiaryForm fromEntity(DiaryEntity entity) {
        if (entity == null)
            return null;
        // 엔티티의 정보들을 DTO의 생성자에 주입하여 객체 생성 및 반환
        return new DiaryForm(
                entity.getDiaryId(),
                entity.getAuthorEmail(),
                entity.getDiaryTitle(),
                entity.getDiaryContent(),
                entity.getAttachedPhotoUrl(),
                entity.getWrittenDate());
    }
}
