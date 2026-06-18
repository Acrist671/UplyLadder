package com.otsos.userservice.controllers;

import com.otsos.userservice.dto.AuthDto;
import com.otsos.userservice.dto.LoginDto;
import com.otsos.userservice.dto.RegisterDto;
import com.otsos.userservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<AuthDto> register(@RequestBody RegisterDto registerDto) {
        AuthDto authDto = authService.registerUser(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
    }

    public ResponseEntity<AuthDto> login(@RequestBody LoginDto loginDto) {
        AuthDto authDto = authService.loginUser(loginDto);

        return ResponseEntity.ok(authDto);
    }

}
