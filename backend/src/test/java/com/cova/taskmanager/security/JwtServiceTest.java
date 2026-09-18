package com.cova.taskmanager.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("test-secret-key-for-unit-tests-only", 3600000);

    @Test
    void generatesTokenThatCanBeValidatedAndReadBack() {
        String token = jwtService.generateToken("jane@example.com");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("jane@example.com");
    }

    @Test
    void rejectsGarbageToken() {
        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
    }
}
