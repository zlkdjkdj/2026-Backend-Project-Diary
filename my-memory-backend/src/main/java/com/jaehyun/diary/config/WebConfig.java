package com.jaehyun.diary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

// 정적 리소스 서빙 경로 및 Spring MVC 동작 제어 설정 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 정적 리소스 URL과 내부 물리적 디렉토리 간 매핑 수행 핸들러 등록
    // param: registry - 스프링 MVC에서 정적 파일 제공 경로를 등록/관리하는 레지스트리 객체
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploadPath: 프로젝트 내부(또는 외부 지정 위치)의 "uploads" 디렉토리에 대한 절대 경로를 문자열로 추출
        String uploadPath = new File("uploads").getAbsolutePath();

        // 클라이언트가 "/uploads/파일명" 과 같이 URL로 요청할 경우,
        // 이를 가로채어 실제 파일 시스템의 uploadPath 물리적 폴더 안에 있는 파일과 매핑시켜 반환하도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
