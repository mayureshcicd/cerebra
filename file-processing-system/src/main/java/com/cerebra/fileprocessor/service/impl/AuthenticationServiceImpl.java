package com.cerebra.fileprocessor.service.impl;


import com.cerebra.fileprocessor.common.ResponseUtil;
import com.cerebra.fileprocessor.dto.SignInRequest;
import com.cerebra.fileprocessor.dto.SignUpRequest;
import com.cerebra.fileprocessor.entity.Role;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.exceptions.RestApiException;
import com.cerebra.fileprocessor.repository.RoleRepository;
import com.cerebra.fileprocessor.repository.UserRepository;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.AuthenticationService;
import com.cerebra.fileprocessor.service.EmailService;
import com.cerebra.fileprocessor.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public RestApiResponse<Map<String, String>> login(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException("Invalid email", HttpStatus.BAD_REQUEST));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new RestApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Map<String, String> tokenMap = getTokenMap(user);

        return ResponseUtil.getResponse(tokenMap, "Employee Login Successfully");
    }

    @Override
    @Transactional
    public RestApiResponse<String> signup(HttpServletRequest requestH, SignUpRequest request) {
        validatePasswords(request);
        validateEmailUniqueness(request.getEmail());

        Role role = resolveRole();
        User user = buildUser(request, role,  requestH.getRemoteAddr());
        user = userRepository.save(user);

        return handlePostSignupActions(user);
    }
    private void validatePasswords(SignUpRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RestApiException("Password and confirm password must be identical.", HttpStatus.BAD_REQUEST);
        }
    }
    private Role resolveRole() {
            return roleRepository.findById(2L).orElse(null);

    }

    private User buildUser(SignUpRequest request, Role role, String ipAddress) {
        return User.builder()
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .tempPassword(request.getNewPassword())
                .role(role)
                .enabled(true)
                .verified(true)
                .userType("USER")
                .ipAddress(ipAddress)
                .build();
    }
    private RestApiResponse<String> handlePostSignupActions(User user) {
            sendLoginCredentialsEmail(user);
            return ResponseUtil.getResponse(String.format("Sent the Login email to %s for login credentials on your registered email %s.", user.getFirstName(),user.getEmail()));
    }

    private void sendLoginCredentialsEmail(User user) {
        emailService.sendEmailLoginCredentials(user.getFirstName(), user.getEmail(), user.getTempPassword(), user.getEmail(), "");
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RestApiException(String.format("%s Email is already in use.", email), HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, String> getTokenMap(User user) {
        return new HashMap<String, String>() {{
            put("accessToken", jwtService.generateToken(user));
            put("refreshToken", jwtService.generateRefreshToken(user));
        }};
    }

}