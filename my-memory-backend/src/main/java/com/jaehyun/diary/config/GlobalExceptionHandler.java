package com.jaehyun.diary.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 전역 예외 포착 및 일관된 HTTP 응답 변환 핸들러
@RestControllerAdvice // 모든 @RestController 계층에서 던져진 예외를 전역적으로 인터셉트하는 AOP 기반 어노테이션
@Slf4j // 로깅 객체(log)를 자동 생성, 에러 기록을 돕는 롬복 어노테이션
public class GlobalExceptionHandler {

    // 잘못된 요청 인자(IllegalArgumentException) 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // 에러 원인을 서버 측 로그(warn 레벨)로 기록하여 추적
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        // 단, 에러 메시지가 "권한이 없습니다."인 경우 소유권 위반으로 간주하여 403 Forbidden 상태 코드로 변경
        if ("권한이 없습니다.".equals(e.getMessage())) {
            status = HttpStatus.FORBIDDEN;
        }

        // 응답 본문에 담을 JSON 형식의 Map 컬렉션 생성 및 메시지 주입
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    // 보안 위반(SecurityException) 예외 처리
    // param: e - 보안 관련 로직 수행 중 포착된 SecurityException 객체
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException e) {
        log.warn("SecurityException occurred: {}", e.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 명시적으로 처리되지 않은 모든 일반 예외(Exception) 처리
    // param: e - 위 핸들러들에 매칭되지 않은 최상위 Exception 예외 객체 (예: 널포인터, DB 타임아웃 등)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        // 알 수 없는 서버 오류이므로 스택 트레이스 전체를 error 레벨로 로깅
        log.error("Unhandled exception occurred: ", e);

        Map<String, String> response = new HashMap<>();
        // 서버의 상세 에러 내역을 감추고 공통 메시지 포맷으로 클라이언트에게 반환
        response.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
