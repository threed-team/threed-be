package com.example.threedbe.common.exception;

import org.springframework.http.HttpStatus;

public class ThreedConflictException extends ThreedException {

	public ThreedConflictException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

}
