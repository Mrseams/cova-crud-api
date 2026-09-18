package com.cova.taskmanager.service;

import com.cova.taskmanager.dto.AuthResponse;
import com.cova.taskmanager.dto.LoginRequest;
import com.cova.taskmanager.dto.RegisterRequest;
import com.cova.taskmanager.entity.User;
import com.cova.taskmanager.exception.EmailAlreadyUsedException;
import com.cova.taskmanager.exception.InvalidCredentialsException;
import com.cova.taskmanager.repository.UserRepository;
import com.cova.taskmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("jane@example.com", "secret123");
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashed");
        when(jwtService.generateToken(registerRequest.email())).thenReturn("fake-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.token()).isEqualTo("fake-token");
        assertThat(response.email()).isEqualTo("jane@example.com");
    }

    @Test
    void registerFailsWhenEmailAlreadyUsed() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void loginFailsWhenPasswordDoesNotMatch() {
        User user = User.builder().email("jane@example.com").password("hashed").build();
        LoginRequest loginRequest = new LoginRequest("jane@example.com", "wrong-password");

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
