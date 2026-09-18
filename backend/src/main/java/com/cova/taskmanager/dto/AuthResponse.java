package com.cova.taskmanager.dto;

public record AuthResponse(
        String token,
        String email
) {
}
