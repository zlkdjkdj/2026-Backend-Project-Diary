package com.jaehyun.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// MyMemory 백엔드 애플리케이션 실행 메인 진입점(Entry Point)
@SpringBootApplication
public class MyMemoryBackendApplication {

    // 애플리케이션 부트스트랩 및 내장 웹 서버 실행
    // param: args - 실행 시 커맨드 라인에서 전달된 인자 배열
    public static void main(String[] args) {
        // SpringApplication.run()을 통해 컨테이너 생명주기가 시작됨
        SpringApplication.run(MyMemoryBackendApplication.class, args);
    }
}
