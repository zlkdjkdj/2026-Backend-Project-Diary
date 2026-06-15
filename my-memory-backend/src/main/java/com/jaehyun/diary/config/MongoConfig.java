package com.jaehyun.diary.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull; // [추가] 스프링 표준 NonNull 라이브러리를 가져옵니다.

// MongoDB 연결 및 설정
// 커스텀 클라이언트 및 DB 이름 빈 등록
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    // MongoDB 접속 주소
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    // 접속할 MongoDB 이름 반환
    @Override
    @NonNull 
    protected String getDatabaseName() {
        return "diarydb"; // 데이터베이스 이름 지정
    }

    // 설정된 URI 기반 MongoDB 클라이언트 인스턴스 생성
    @Override
    @NonNull 
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}