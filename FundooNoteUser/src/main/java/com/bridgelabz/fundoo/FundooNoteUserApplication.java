package com.bridgelabz.fundoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class FundooNoteUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundooNoteUserApplication.class, args);
	}

}
