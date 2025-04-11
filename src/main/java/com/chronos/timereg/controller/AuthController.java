package com.chronos.timereg.controller;

import com.chronos.timereg.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestParam("grant_type") String grantType,
                                      @RequestParam("username") String username,
                                      @RequestParam("password") String password) {
        if (!"password".equalsIgnoreCase(grantType)) {
            return ResponseEntity.badRequest().body("Unsupported grant_type");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtUtil.generateToken(username);
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", token);
            response.put("token_type", "bearer");
            response.put("expires_in", jwtUtil.getJwtExpirationInMs() / 1000);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
