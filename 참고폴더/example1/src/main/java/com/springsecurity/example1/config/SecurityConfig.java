package com.springsecurity.example1.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CSRF 해제 (개발용) & H2 콘솔 접근 허용
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console()) // H2 콘솔 예외 처리
                .disable() // 실습을 위해 비활성화 (운영환경에선 켜는 걸 권장)
        );

        // 2. H2 콘솔 화면 깨짐 방지 (iframe 허용)
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        // 3. 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // 정적 리소스(css, js) 및 H2 콘솔은 모두 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                // 로그인 페이지, 회원가입 페이지는 모두 허용
                .requestMatchers("/login", "/join", "/joinProc","/","/intro").permitAll()
                // 그 외 요청은 인증된 사용자만 접근 가능
                .anyRequest().authenticated()
        );

        // 4. 폼 로그인 설정
        http.formLogin(login -> login
                .loginPage("/login")             // 사용자 정의 로그인 페이지 (Controller 매핑 필요)
                .loginProcessingUrl("/loginProc") // HTML Form의 action 주소 (스프링 시큐리티가 가로챔)
                .usernameParameter("loginId")    // HTML Form의 input name (기본값 username -> loginId로 변경)
                .passwordParameter("password")   // HTML Form의 input name
                .defaultSuccessUrl("/", true)    // 로그인 성공 시 이동할 주소
                .permitAll()
        );

        // 5. 폼 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/logout")         // 로그아웃 요청을 처리할 URL (HTML에서 <a href="/logout">)
                .logoutSuccessUrl("/")        // ★ 로그아웃 성공 시 이동할 페이지 (메인으로 이동)
                .invalidateHttpSession(true)  // 로그아웃 시 세션 제거
                .deleteCookies("JSESSIONID")  // 쿠키(세션ID) 제거
        );

        return http.build();
    }
}