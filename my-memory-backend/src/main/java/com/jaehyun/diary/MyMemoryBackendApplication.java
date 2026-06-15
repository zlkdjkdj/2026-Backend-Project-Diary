package com.jaehyun.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 메인 진입점
@SpringBootApplication
public class MyMemoryBackendApplication {

    static {
        // 로컬 환경을 위한 .env 파일 로드
        java.io.File envFile = new java.io.File("../.env");
        if (!envFile.exists()) {
            envFile = new java.io.File(".env");
        }
        if (envFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // 주석(#)을 제외하고 key=value 포맷을 추출하여 시스템 프로퍼티로 설정
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

    public static void main(String[] args) {
        SpringApplication.run(MyMemoryBackendApplication.class, args);
    }
}
