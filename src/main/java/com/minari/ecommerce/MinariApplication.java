package com.minari.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.minari.ecommerce.repository")
@ComponentScan(basePackages = "com.minari.ecommerce")
public class MinariApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinariApplication.class, args);
	}

}
