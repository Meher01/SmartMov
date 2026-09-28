package com.SmartMov.SmartMov_backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.SmartMov")
@EntityScan("com.SmartMov.entity")
@EnableJpaRepositories("com.SmartMov.repository")
public class SmartMovBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartMovBackendApplication.class, args);
	}

}
