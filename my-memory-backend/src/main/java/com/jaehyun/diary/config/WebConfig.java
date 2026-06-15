package com.jaehyun.diary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

// 정적 리소스 서빙 경로 및 Spring MVC 동작 제어 설정 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 정적 리소스 URL과 내부 물리적 디렉토리 간 매핑 수행 핸들러 등록
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploadPath: 디렉토리 절대 경로 추출
        String uploadPath = new File("uploads").getAbsolutePath();

        // 클라이언트가 "/uploads/파일명" 과 같이 URL로 요청할 경우,
        // 이를 가로채어 실제 파일 시스템의 uploadPath 물리적 폴더 안에 있는 파일과 매핑시켜 반환하도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
