package com.example.threedbe.common.exception;

import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		String exceptionMessage = exception.getAllErrors().stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining(" | "));

		log.warn("message: {}", exceptionMessage);

		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessage);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ProblemDetail handleNoResourceFoundException(NoResourceFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(ThreedException.class)
	public ProblemDetail handleCustomException(ThreedException exception) {
		log.warn("message: {}", exception.getMessage());

		return ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleException(Exception exception) {
		log.error("exception: {}", exception);

		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러");
	}

}
