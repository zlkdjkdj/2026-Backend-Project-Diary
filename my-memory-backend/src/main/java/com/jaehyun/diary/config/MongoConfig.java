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
        String mongoUri = System.getenv("MONGO_URI");
        if (mongoUri == null || mongoUri.isEmpty()) {
            throw new IllegalArgumentException("MONGO_URI environment variable is not set!");
        }
        return MongoClients.create(mongoUri);
    }
}
