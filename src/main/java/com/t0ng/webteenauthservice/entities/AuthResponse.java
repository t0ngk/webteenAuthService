package com.t0ng.webteenauthservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Boolean isError;
    private String message;
    private String accessToken;
    private String refreshToken;
}
