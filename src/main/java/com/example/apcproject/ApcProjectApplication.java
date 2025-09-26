package com.example.apcproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing   // ✅ Enable createdAt & lastModifiedDate auto-handling
public class ApcProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApcProjectApplication.class, args);
    }
    
}
