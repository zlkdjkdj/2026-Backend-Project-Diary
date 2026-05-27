package com.springsecurity.example2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 헤더에서 Authorization 토큰 꺼내기
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("1. 헤더 수신 확인: " + authorization); // 로그 추가

        // 2. 토큰이 없거나 Bearer로 시작하지 않으면 패스
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("2. 토큰이 없거나 Bearer로 시작하지 않음"); // 로그 추가
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 뒷부분(Token) 추출
        String token = authorization.split(" ")[1];
        System.out.println("3. 추출된 토큰: " + token); // 로그 추가

        // 4. 토큰 유효성 검사 및 만료 체크
        if (!jwtUtil.validateToken(token)) {
            System.out.println("4. 토큰이 유효하지 않음 (만료되거나 위조됨)"); // 로그 추가
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 토큰에서 ID 꺼내기
        String loginId = jwtUtil.getLoginId(token);
        System.out.println("5. 토큰 사용자 ID: " + loginId); // 로그 추가

        // 6. UserDetails 가져오기 (DB 조회)
        // JWT에 권한 정보를 모두 넣어서 DB 조회를 안 할 수도 있지만,
        // 실시간 권한 변경 반영을 위해 보통 DB 조회를 겸합니다.
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);

        // 7. 인증 객체 생성 및 SecurityContext에 저장
        if (userDetails != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("6. 인증 객체 저장 완료!"); // 로그 추가
        }

        filterChain.doFilter(request, response);
    }
}