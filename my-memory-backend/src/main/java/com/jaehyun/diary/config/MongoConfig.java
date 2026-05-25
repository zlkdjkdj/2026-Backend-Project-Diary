package com.jaehyun.diary.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "diarydb";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb+srv://zlkdjkdj:kil34530@cluster0.6fsxsk9.mongodb.net/diarydb?retryWrites=true&w=majority");
    }
}
