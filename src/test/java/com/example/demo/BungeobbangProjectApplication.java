package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({
    "classpath:applicationContext.xml",
    "classpath:ds-servlet.xml", // 주의: 필요에 따라 경로 수정
})
public class BungeobbangProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(BungeobbangProjectApplication.class, args);
    }
}
