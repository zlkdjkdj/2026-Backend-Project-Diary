package com.jaehyun.diary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// MongoDB 'users' 컬렉션 매핑 사용자 영속성 엔티티
@Document(collection = "users") // MongoDB의 users 컬렉션에 이 엔티티의 데이터가 저장됨을 지정
@Data
@Builder // 빌더 패턴을 통한 유연한 객체 생성 지원
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 생성자 자동 생성
public class UserEntity {

    // MongoDB 문서 고유 식별자 (Object ID)
    // DB 저장 시 MongoDB가 자동 생성하는 고유 키와 매핑
    @Id
    private String userId;

    // 사용자 고유 이메일 계정, id 로 사용
    private String userEmail;

    // 단방향 해시 암호화 비밀번호
    private String encryptedPassword;

    // 시스템 표시용 사용자 닉네임
    private String userNickname;

    // 시스템 접근 수준 정의 인가(Authorization) 역할
    private UserRole userRole;
}
