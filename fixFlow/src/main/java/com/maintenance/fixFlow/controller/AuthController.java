package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.LoginRequestDto;
import com.maintenance.fixFlow.dto.LoginResponseDto;
import com.maintenance.fixFlow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authservice;

    public AuthController(AuthService authservice){
        this.authservice=authservice;
    }

    @PostMapping("/login")
    public LoginResponseDto login(
            @Valid @RequestBody LoginRequestDto dto) {

        String token = authservice.login(dto);

        return new LoginResponseDto(token);
    }
}