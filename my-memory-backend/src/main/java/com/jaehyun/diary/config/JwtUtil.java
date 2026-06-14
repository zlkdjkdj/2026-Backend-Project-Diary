package com.jaehyun.diary.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT 생성, 파싱 및 유효성 검증 유틸리티 클래스
// JWT를 생성하고 검증하며 데이터를 추출하는 공통 기능 객체
@Component // 스프링 컨테이너에 의해 싱글톤 빈으로 관리되도록 선언
public class JwtUtil {

    // secretKey: application.yml에서 'jwt.secret' 값을 주입받음
    @Value("${jwt.secret}")
    private String secretKey;

    // expirationTime 토큰의 유효 기간(밀리초)
    @Value("${jwt.expiration}")
    private long expirationTime;

    // 암호화된 내부 키 객체
    private Key key;

    // 설정된 비밀키 기반 암호화 키 객체 초기화
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 사용자 이메일 및 역할 정보를 포함하는 신규 JWT 발급
    public String createToken(String email, String role) {
        // 클레임(Claims): JWT의 페이로드(Body)에 담길 정보의 조각들. 빈 클레임 객체를 생성
        Claims claims = Jwts.claims();
        // 핵심 데이터 삽입
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims) // 데이터(Payload) 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간 기록
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 유효 기간(만료 시간) 기록
                .signWith(key, SignatureAlgorithm.HS256) // 생성해둔 key와 HS256 알고리즘을 이용해 무결성 보장용 디지털 서명 추가
                .compact(); // 최종적으로 압축하여 JWT 문자열로 반환
    }

    // JWT 내 사용자 이메일 정보 추출
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    // JWT 내 사용자 역할 정보 추출
    // param: token - 클라이언트로부터 받은 검증 대상 JWT 문자열
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class); // 토큰의 Payload(Claims) 영역에 저장된 "role" 값 문자열
    }

    // JWT 서명 무결성 및 만료 여부 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 만료, 토큰 손상 등의 문제 발생 시 false 반환
            return false;
        }
    }

    // JWT 문자열 파싱 및 페이로드(Claims) 객체 반환
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 파싱 시 반드시 서버가 보유한 동일한 key로 서명 대조 검증
                .build()
                .parseClaimsJws(token)
                .getBody(); // 무결성 통과 후 Payload 데이터 추출
    }
}
