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
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// JWT 필터 
// 모든 HTTP 요청을 가로채서 JWT 토큰을 확인하고 인증 정보를 설정하는 필터
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) // http 요청이 컨트롤러에 도착하기 전에 실행되는 메서드
            throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰 헤더 검사
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String token = authorization.split(" ")[1];

        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 토큰에서 인증 정보 추출
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        // 인증 객체 등록
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

        // 요청 정보 설정
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 다음 필터 혹은 컨트롤러로 요청 넘김
        filterChain.doFilter(request, response);
    }
}
