package com.example.threedbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.threedbe.auth.config.GitHubOAuthProperties;
import com.example.threedbe.auth.config.GoogleOAuthProperties;
import com.example.threedbe.auth.config.KakaoOAuthProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	GoogleOAuthProperties.class,
	KakaoOAuthProperties.class,
	GitHubOAuthProperties.class
})
public class ThreedBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreedBeApplication.class, args);
	}
}
