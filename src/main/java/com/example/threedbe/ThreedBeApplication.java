package com.example.threedbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.threedbe.auth.config.GoogleOAuthProperties;

@SpringBootApplication
@EnableConfigurationProperties(GoogleOAuthProperties.class)
public class ThreedBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreedBeApplication.class, args);
	}
}
