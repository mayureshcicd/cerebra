package com.cerebra.fileprocessor.controller;


import com.cerebra.fileprocessor.dto.SignInRequest;
import com.cerebra.fileprocessor.dto.SignUpRequest;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class.getName());
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/registration")
    public ResponseEntity<RestApiResponse<String>> signup(HttpServletRequest requestH,
                                                          @Valid @RequestBody SignUpRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(
                authenticationService.signup(requestH, request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RestApiResponse<Map<String, String>>>login(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                authenticationService.login(request));
    }

}
