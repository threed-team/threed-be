package com.example.threedbe.common.exception;

import org.springframework.http.HttpStatus;

public class ThreedServerErrorException extends ThreedException {

	public ThreedServerErrorException(String message) {
		super(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
