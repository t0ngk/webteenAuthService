package com.t0ng.webteenauthservice.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.t0ng.webteenauthservice.db.User;
import com.t0ng.webteenauthservice.db.UserService;
import com.t0ng.webteenauthservice.entities.AuthResponse;
import com.t0ng.webteenauthservice.entities.UserResponse;
import com.t0ng.webteenauthservice.rest.LoginRestModel;
import com.t0ng.webteenauthservice.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserService userService;

    @RabbitListener(queues = "loginQueue")
    public AuthResponse login(LoginRestModel request) {
        System.out.println("Login request: " + request);
        System.out.println("Secret key: " + secretKey);
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return new AuthResponse(true, "User not found", null, null);
        }
        BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(), user.getPassword());
        if (!result.verified) {
            return new AuthResponse(true, "Wrong password", null, null);
        }
        Map<String, Object> claims = Map.of("_id", user.get_id(),"username", user.getUsername(), "role", user.getRole());
        String accessToken = new JwtUtil(secretKey, "8400").generateToken(claims, user.get_id(), "access");
        String refreshToken = new JwtUtil(secretKey, "8400").generateToken(claims, user.get_id(), "refresh");
        return new AuthResponse(false, "Login success", accessToken, refreshToken);
    }

    @RabbitListener(queues = "profileQueue")
    public UserResponse profile(String token) {
        try {
            JwtUtil jwtUtil = new JwtUtil(secretKey, "8400");
            Claims profile = jwtUtil.parseToken(token);
            User user = userService.findById(profile.getSubject());
            if (user == null) {
                return null;
            }
            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(user.getEmail());
            userResponse.setUsername(user.getUsername());
            userResponse.setRole(user.getRole());
            userResponse.setBirthDate(user.getBirthDate());
            userResponse.setCreatedDate(user.getCreatedDate());
            return userResponse;
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    @RabbitListener(queues = "refreshQueue")
    public AuthResponse newRefreshToken(String token) {
        try {
            Claims profile = new JwtUtil(secretKey, "8400").parseToken(token);
            User user = userService.findById(profile.getSubject());
            if (user == null) {
                return new AuthResponse(true, "User not found", null, null);
            }
            Map<String, Object> claims = Map.of("_id", user.get_id(),"username", user.getUsername(), "role", user.getRole());
            String accessToken = new JwtUtil(secretKey, "8400").generateToken(claims, user.get_id(), "access");
            String refreshToken = new JwtUtil(secretKey, "8400").generateToken(claims, user.get_id(), "refresh");
            return new AuthResponse(false, "Refresh success", accessToken, refreshToken);
        } catch (ExpiredJwtException e) {
            return new AuthResponse(true, "Token is expired", null, null);
        }
    }
}
