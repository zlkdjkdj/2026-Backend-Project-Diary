package com.jaehyun.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// MyMemory 백엔드 애플리케이션 실행 메인 진입점(Entry Point)
@SpringBootApplication
public class MyMemoryBackendApplication {

    static {
        // 로컬 실행환경 지원을 위해 .env 파일 로드 및 시스템 프로퍼티 등록
        java.io.File envFile = new java.io.File("../.env");
        if (!envFile.exists()) {
            envFile = new java.io.File(".env");
        }
        if (envFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        System.setProperty(key, value);
                    }
                }
                System.out.println(".env 환경변수 시스템 등록 성공!");
            } catch (java.io.IOException e) {
                System.err.println(".env 로딩 실패: " + e.getMessage());
            }
        }
    }

    // 애플리케이션 부트스트랩 및 내장 웹 서버 실행
    // param: args - 실행 시 커맨드 라인에서 전달된 인자 배열
    public static void main(String[] args) {
        // SpringApplication.run()을 통해 컨테이너 생명주기가 시작됨
        SpringApplication.run(MyMemoryBackendApplication.class, args);
    }
}
