package com.t0ng.webteenauthservice.rest;

import com.t0ng.webteenauthservice.entities.AuthResponse;
import com.t0ng.webteenauthservice.entities.UserResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthController {
    private RabbitTemplate rabbitTemplate;

    public AuthController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRestModel request) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = rabbitTemplate.getMessageConverter().toMessage(request, messageProperties);
        try {
            Object response = rabbitTemplate.convertSendAndReceive("Direct","login", message);
            AuthResponse authResponse = (AuthResponse) response;
            if (authResponse.getIsError()) {
                Map<String, String> error = new HashMap<>();
                error.put("isError", authResponse.getIsError().toString());
                error.put("message", authResponse.getMessage());
                return ResponseEntity.badRequest().body(error);
            } else {
                return ResponseEntity.ok(authResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("isError", "true");
            error.put("message", e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader("Authorization") String token) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        token = token.substring(7);
        Message message = rabbitTemplate.getMessageConverter().toMessage(token, messageProperties);
        System.out.println("Token: " + token);
        try {
            Object response = rabbitTemplate.convertSendAndReceive("Direct","profile", message);
            UserResponse userResponse = ((UserResponse) response);
            if (userResponse == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found or token expired");
                return ResponseEntity.badRequest().body(error);
            }
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("isError", "true");
            error.put("message", e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String token) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        token = token.substring(7);
        Message message = rabbitTemplate.getMessageConverter().toMessage(token, messageProperties);
        System.out.println("Token: " + token);
        try {
            Object response = rabbitTemplate.convertSendAndReceive("Direct","refresh", message);
            AuthResponse authResponse = (AuthResponse) response;
            if (authResponse.getIsError()) {
                Map<String, String> error = new HashMap<>();
                error.put("isError", authResponse.getIsError().toString());
                error.put("message", authResponse.getMessage());
                return ResponseEntity.badRequest().body(error);
            } else {
                return ResponseEntity.ok(authResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("isError", "true");
            error.put("message", e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
