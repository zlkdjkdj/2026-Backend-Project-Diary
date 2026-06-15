package com.jaehyun.diary.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// HTTP 요청에 대한 JWT 기반 인증 보안 필터, 토큰의 유효성을 검사, 통과시 Spring Security 컨텍스트에 인증 객체를 등록
@RequiredArgsConstructor // 의존성 주입 역할
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Authorization 헤더 검사 및 유효 토큰 시 사용자 인증 정보 등록
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 헤더 중 "Authorization" 키워드의 값(토큰 문자열)을 가져옴
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더x, Bearer x 이면
        // 인증처리 x, 그대로 다음 필터로 넘김
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 형식에서 공백 기준으로 잘라 실제 토큰 문자열(<token>)만 추출
        String token = authorization.split(" ")[1];

        // 추출된 토큰이 손상되었거나 만료되었는지 검증
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 유효한 토큰임이 확인, 토큰 내부에 숨겨진 사용자 정보(이메일, 권한)를 안전하게 추출
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        // SecurityContext에 보관할 스프링 시큐리티 전용 인증 객체(Authentication) 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

        // 현재 요청에 대한 세부적인 부가 정보(IP 등)를 인증 객체에 덧붙임
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 해당 인증 객체를 최종 세팅, 인증된 사용자로 확인
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //요청 계속 진행
        filterChain.doFilter(request, response);
    }
}
