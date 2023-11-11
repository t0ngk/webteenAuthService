package com.t0ng.webteenauthservice.rest;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginRestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 7471356245797779449L;
    private String username;
    private String password;
}
