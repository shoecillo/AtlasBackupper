package com.sh.atlas.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtlasBckaupApp {
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(AtlasBckaupApp.class, args);
	}
	
}
