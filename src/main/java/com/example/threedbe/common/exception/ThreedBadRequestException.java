package com.example.threedbe.common.exception;

import org.springframework.http.HttpStatus;

public class ThreedBadRequestException extends ThreedException {

	public ThreedBadRequestException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

}
