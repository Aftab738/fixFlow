package com.maintenance.fixFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FixFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(FixFlowApplication.class, args);
		System.out.println("Working!");
	}

}
