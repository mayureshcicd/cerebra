package com.cerebra.fileprocessor.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cerebra.fileprocessor.response.RestApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final WebRequest webRequest;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String result=request.getAttribute("token")==null?"You are unauthorized":request.getAttribute("token").toString();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(RestApiResponse.builder().timestamp(new Date()).message(result)
                .errors(webRequest.getDescription(false)).build()));

    }
}
