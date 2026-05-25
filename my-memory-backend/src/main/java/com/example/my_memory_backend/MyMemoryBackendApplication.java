package com.example.my_memory_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.my_memory_backend", "com.jaehyun.diary"})
@EnableMongoRepositories(basePackages = "com.jaehyun.diary.repository")
public class MyMemoryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMemoryBackendApplication.class, args);
	}

}
