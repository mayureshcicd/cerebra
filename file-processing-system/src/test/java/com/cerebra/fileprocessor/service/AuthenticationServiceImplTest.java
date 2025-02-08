package com.cerebra.fileprocessor.service;


import com.cerebra.fileprocessor.dto.SignInRequest;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.exceptions.RestApiException;
import com.cerebra.fileprocessor.repository.RoleRepository;
import com.cerebra.fileprocessor.repository.UserRepository;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        RestApiResponse<Map<String, String>> response = authenticationService.login(signInRequest);

        assertEquals("Employee Login Successfully", response.getMessage());
        assertEquals("accessToken", response.getData().get("accessToken"));
        assertEquals("refreshToken", response.getData().get("refreshToken"));

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(jwtService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    void testLogin_InvalidEmail() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("invalid@example.com");
        signInRequest.setPassword("password");
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, () -> {
            authenticationService.login(signInRequest);
        });

        assertEquals("Invalid email", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
    }

}