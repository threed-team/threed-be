package com.example.threedbe.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.ProblemDetail;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
	responseCode = "404",
	content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
public @interface SwaggerErrorCode404 {

	@AliasFor(annotation = ApiResponse.class, attribute = "description")
	String description();

}
