package com.springsecurity.example2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 1. 기본 생성자 접근 제어
@AllArgsConstructor
@Table(name="UserMember")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 컬럼 제약조건 추가 (유니크, null 불가)
    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    // 3. Enum 타입 매핑 설정 (가장 중요!)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}