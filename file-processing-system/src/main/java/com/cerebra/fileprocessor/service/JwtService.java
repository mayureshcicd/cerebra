package com.cerebra.fileprocessor.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;


public interface JwtService {

    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String getAttributeValue(String token, String attribute);

    boolean isTokenExpired(String token);

    String getAuthorization(HttpServletRequest requestH);
}