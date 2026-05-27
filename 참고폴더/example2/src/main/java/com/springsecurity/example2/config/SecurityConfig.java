package com.springsecurity.example2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CSRF 비활성화 (REST API는 보통 비활성화)
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. Form Login 및 HttpBasic 비활성화 (JSON 기반 통신이므로)
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 3. 세션 사용 안 함 (STATELESS) 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 4. H2 콘솔 허용
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        // 5. 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/api/login", "/api/join").permitAll() // 로그인, 회원가입 허용
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자만 접근 가능
                .anyRequest().authenticated()
        );

        // 6. JWT 필터 등록 (UsernamePasswordAuthenticationFilter 앞에서 동작)
        http.addFilterBefore(new JwtFilter(userDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}