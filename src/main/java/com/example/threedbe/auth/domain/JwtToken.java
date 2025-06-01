package com.example.threedbe.auth.domain;

import com.example.threedbe.auth.config.AuthProperties;

public interface JwtToken {

	String getSecretKey(AuthProperties authProperties);

	String getValue();

}
