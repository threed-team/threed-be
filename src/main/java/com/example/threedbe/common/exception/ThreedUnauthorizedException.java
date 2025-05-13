package com.example.threedbe.common.exception;

import org.springframework.http.HttpStatus;

public class ThreedUnauthorizedException extends ThreedException {

	public ThreedUnauthorizedException(String message) {
		super(message, HttpStatus.UNAUTHORIZED);
	}

}
