package com.t0ng.webteenauthservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private String secertKey;
    private String expirationTime;
    private Key key;

    public JwtUtil(@Value("${jwt.secret}") String secertKey, @Value("${jwt.expiration}") String expirationTime) {
        this.secertKey = secertKey;
        this.key = Keys.hmacShaKeyFor(secertKey.getBytes());
        this.expirationTime = expirationTime;
    }

    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return parseToken(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String generateToken(Map<String, Object> claims, String subject, String type) {
        long expirationTimeLong = Long.parseLong(expirationTime) * 1000;
        if (type.equals("access")) {
            expirationTimeLong = Long.parseLong(expirationTime);
        } else if (type.equals("refresh")) {
            expirationTimeLong = Long.parseLong(expirationTime) * 5;
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTime) * 1000))
                .signWith(key)
                .compact();

    }
}
