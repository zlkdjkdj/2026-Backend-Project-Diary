package com.jaehyun.diary.entity;

// 시스템 사용자 권한 수준 정의 열거형(Enum)
// 사용자별로 등급을 나누어 Spring Security 등의 인가 정책에서 리소스 접근을 제어할 때 기준으로 사용되는 객체
public enum UserRole {
    // 일반 사용자 권한: 기본 일기 작성/조회 권한
    USER,

    // 시스템 관리자 권한: 애플리케이션 전체 관리 권한
    ADMIN
}
