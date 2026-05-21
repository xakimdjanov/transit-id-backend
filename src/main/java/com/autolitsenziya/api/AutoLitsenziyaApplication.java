package com.autolitsenziya.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoLitsenziyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoLitsenziyaApplication.class, args);
    }
}
