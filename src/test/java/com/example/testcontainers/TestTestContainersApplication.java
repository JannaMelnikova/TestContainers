package com.example.testcontainers;

import org.springframework.boot.SpringApplication;

public class TestTestContainersApplication {

    public static void main(String[] args) {
        SpringApplication.from(TestContainersApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
