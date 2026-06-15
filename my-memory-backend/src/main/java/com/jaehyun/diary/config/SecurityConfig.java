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

// Spring Security 프레임워크 전반 인증/인가 정책 설정
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // HTTP 보안 필터 체인 및 인증 정책 구성
    // Spring Security의 HttpSecurity 빌더 객체로, 보안 규칙을 체이닝 방식으로 설정할 수 있게 해줌
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 기본 제공되는 로그인 폼(화면) 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        // 기본 HTTP Basic 인증 비활성화 
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 세션 정책을 STATELESS(무상태)로 설정.
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // HTTP 요청 경로에 따른 접근 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인 등의 인증 API는 로그인 없이 누구든 접근 가능(permitAll)
                .requestMatchers("/api/auth/**").permitAll()
                // 그 외의 모든 요청은 유효한 토큰 기반의 인증된(authenticated) 사용자만 접근 가능
                .anyRequest().authenticated());

        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 설정된 체인 규칙들을 반영하여 SecurityFilterChain 생성 반환
        return http.build();
    }

    // 교차 출처 리소스 공유(CORS) 정책 설정 소스 빈 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // GET, POST 등 나열된 주요 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 모든 HTTP 헤더허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 위에서 만든 정책(configuration)을 "/**" (모든 경로)에 일괄 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
