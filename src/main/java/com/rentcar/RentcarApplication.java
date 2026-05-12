package com.rentcar;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableScheduling
@SpringBootApplication
public class RentcarApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentcarApplication.class, args);
	}

}
