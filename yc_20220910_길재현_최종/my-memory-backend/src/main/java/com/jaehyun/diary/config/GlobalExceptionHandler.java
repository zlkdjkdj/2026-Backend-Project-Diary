package com.jaehyun.diary.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 예외 처리 핸들러
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 인자 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        // 권한 에러 매핑
        if ("권한이 없습니다.".equals(e.getMessage())) {
            status = HttpStatus.FORBIDDEN;
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    // 보안 예외 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException e) {
        log.warn("SecurityException occurred: {}", e.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        log.error("Unhandled exception occurred: ", e);

        Map<String, String> response = new HashMap<>();
        // 에러 메시지 포맷팅
        response.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
