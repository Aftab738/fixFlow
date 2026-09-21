package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.LoginRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String login(LoginRequestDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        return jwtService.generateToken(dto.getEmail());
    }
}