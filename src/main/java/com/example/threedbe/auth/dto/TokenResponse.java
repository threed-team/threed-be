package com.example.threedbe.auth.dto;

import com.example.threedbe.member.dto.response.UserResponse;

public record TokenResponse(String accessToken, UserResponse user) {
}
