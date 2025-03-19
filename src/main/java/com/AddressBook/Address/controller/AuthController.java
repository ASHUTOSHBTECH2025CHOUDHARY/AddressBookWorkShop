package com.AddressBook.Address.controller;

import com.AddressBook.Address.dto.ForgotPasswordDTO;
import com.AddressBook.Address.dto.ResetPasswordDTO;
import com.AddressBook.Address.dto.UserDTO;
import com.AddressBook.Address.dto.LoginDTO;
import com.AddressBook.Address.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.registerUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.loginUser(loginDTO));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordDTO.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordDTO));
    }
}