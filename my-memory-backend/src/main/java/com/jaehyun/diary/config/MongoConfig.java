package com.jaehyun.diary.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.lang.NonNull;

// 몽고DB 설정
@Configuration
@SuppressWarnings("null")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    // 사용할 MongoDB 데이터베이스 이름 정의
    @Override
    @NonNull
    protected String getDatabaseName() {
        return "diarydb";
    }

    // MongoDB에 연결할 MongoClient 객체 생성 및 반환
    @Override
    @NonNull
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}