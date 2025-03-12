package com.gulu.internship07;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan("com.gulu.internship07.entity")
@EnableJpaRepositories("com.gulu.internship07.repository")
@EnableTransactionManagement
public class Internship07Application {

	public static void main(String[] args) {
		SpringApplication.run(Internship07Application.class, args);
	}

}
