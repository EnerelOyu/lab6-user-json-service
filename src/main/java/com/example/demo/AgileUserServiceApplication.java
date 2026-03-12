package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class AgileUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgileUserServiceApplication.class, args);
	}

}
