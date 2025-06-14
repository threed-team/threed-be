package com.example.threedbe.common.config;

import static org.springframework.http.HttpHeaders.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.threedbe.common.resolver.CurrentMemberArgumentResolver;
import com.example.threedbe.common.resolver.LoginMemberArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginMemberArgumentResolver loginMemberArgumentResolver;
	private final CurrentMemberArgumentResolver currentMemberArgumentResolver;

	@Value("${security.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginMemberArgumentResolver);
		resolvers.add(currentMemberArgumentResolver);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins(allowedOrigins)
			.allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD")
			.allowedHeaders("*")
			.allowCredentials(true)
			.exposedHeaders(AUTHORIZATION)
			.maxAge(3600);
	}

}
