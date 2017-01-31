package com.ryanluedders.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImagesourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImagesourceApplication.class, args);
	}
	
}
