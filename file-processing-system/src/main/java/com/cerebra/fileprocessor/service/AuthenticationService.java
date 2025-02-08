package com.cerebra.fileprocessor.service;


import com.cerebra.fileprocessor.dto.SignInRequest;
import com.cerebra.fileprocessor.dto.SignUpRequest;
import com.cerebra.fileprocessor.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthenticationService {

    RestApiResponse<Map<String, String>> login(SignInRequest request);

    RestApiResponse<String> signup(HttpServletRequest requestH, SignUpRequest request);

}