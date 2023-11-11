package com.t0ng.webteenauthservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

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

    public String generateAccessToken(String _id) {
        return Jwts.builder()
                .setSubject(_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTime) * 1000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String _id) {
        return Jwts.builder()
                .setSubject(_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTime) * 1000 * 5))
                .signWith(key)
                .compact();
    }
}
