package com.cerebra.fileprocessor.service.impl;


import com.cerebra.fileprocessor.common.EncryptAndDecrypt;
import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final ConfigProperties configProperties;

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return jwtToken(userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return jwtRefreshToken(userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = EncryptAndDecrypt.decrypt(extractUserName(token));
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public String getAttributeValue(String token, String attribute) {
        String jwt = token.substring(7);
        Claims claims = extractAllClaims(jwt);
        return EncryptAndDecrypt.decrypt(claims.get(attribute, String.class));
    }

    @Override
    public String getAuthorization(HttpServletRequest requestH) {
        String authorization = requestH.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            authorization = requestH.getHeader("authorization");
        }
        return authorization;
    }

    private String generateNewToken(Claims claims, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> populateCommonClaims(UserDetails userDetails) {
        User user = (User) userDetails;
        return new HashMap<String, Object>() {{
            put("name", EncryptAndDecrypt.encrypt(user.getFirstName()));
            put("email", EncryptAndDecrypt.encrypt(user.getEmail()));
            put("stompUser", user.getEmail());
            put("activeRole", EncryptAndDecrypt.encrypt(user.getRole().getName()));
            put("loginUser", user.getFirstName());
            put("loginRole", user.getRole().getName());
        }};
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String jwtToken(UserDetails userDetails) {

        return Jwts
                .builder()
                .setClaims(populateCommonClaims(userDetails))
                .setSubject(EncryptAndDecrypt.encrypt(userDetails.getUsername()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + configProperties.getTokenExpirationMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String jwtRefreshToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(populateCommonClaims(userDetails))
                .setSubject(EncryptAndDecrypt.encrypt(userDetails.getUsername()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + configProperties.getTokenRefreshExpirationMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(configProperties.getTokenSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}