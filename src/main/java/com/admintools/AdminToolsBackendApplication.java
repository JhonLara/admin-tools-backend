package com.admintools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AdminToolsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminToolsBackendApplication.class, args);
    }
}
