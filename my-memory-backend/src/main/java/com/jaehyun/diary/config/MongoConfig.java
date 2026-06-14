package com.jaehyun.diary.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.beans.factory.annotation.Value;

// MongoDB 연결 및 설정
// 역할: application.yml에 명시된 URI를 바탕으로 MongoDB 연결 객체(MongoClient)를 빈으로 등록하고, 사용될 기본 데이터베이스를 지정하는 설정 클래스
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    // mongoUri: 프로퍼티 파일에서 읽어온 MongoDB 접속 주소
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    // 접속할 MongoDB 데이터베이스 이름 반환
    @Override
    protected String getDatabaseName() {
        return "diarydb"; // 'diarydb'라는 이름의 데이터베이스를 사용하도록 강제함
    }

    // 설정된 URI 기반 MongoDB 클라이언트 인스턴스 생성
    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}
