package com.sardul3.hireboost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HireboostApplication {

	public static void main(String[] args) {
		SpringApplication.run(HireboostApplication.class, args);
	}

}
