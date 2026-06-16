package com.jaehyun.diary.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// 보안 설정
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil; // JWT 생성 및 검증을 담당하는 유틸 객체 주입

    @Bean // 비밀번호 암호화 빈 등록
    public BCryptPasswordEncoder passwordEncoder() { // 비밀번호를 해시 함수로 암호화
        return new BCryptPasswordEncoder();
    }

    // Spring Security의 필터링 규칙(FilterChain)을 구성하는 빈
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable); // csrf 방지 비활성화
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.formLogin(AbstractHttpConfigurer::disable); // 로그인 폼 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable); // 기본 http 인증 비활성화

        // 세션을 사용하지 않고 JWT 토큰 방식을 사용하도록 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인가 정책 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // 모든 접근 허용
                .anyRequest().authenticated()); // 그 외 API 요청은 인증을 받아야 접근 가능

        // 앞에 JwtFilter가 먼저 동작하도록 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 특정 프론트엔드 출처(Origin) 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://2026-backend-project-diary.vercel.app"));
        // 허용할 HTTP 메서드 목록 정의
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 모든 요청 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 쿠키 및 인증 헤더(Authorization)를 CORS 요청에 포함 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 URL 경로에 대해 위 CORS 설정 적용
        return source;
    }
}
