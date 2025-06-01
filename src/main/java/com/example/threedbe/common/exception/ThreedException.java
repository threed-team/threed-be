package com.example.threedbe.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class ThreedException extends RuntimeException {

	private final HttpStatus httpStatus;

	public ThreedException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
