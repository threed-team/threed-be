package com.example.threedbe.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
	name = "Authorization",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer"
)
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().addServersItem(new Server().url("/")).info(getInfo());
	}

	private Info getInfo() {
		return new Info().title("Threed API 문서")
			.version("1.0.0");
	}

}
