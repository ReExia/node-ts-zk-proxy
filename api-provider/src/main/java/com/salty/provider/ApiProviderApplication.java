package com.salty.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ApiProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiProviderApplication.class, args);
    }
}
