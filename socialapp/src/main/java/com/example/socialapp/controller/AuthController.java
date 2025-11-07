package com.example.socialapp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socialapp.dto.UserDto;
import com.example.socialapp.entity.User;
import com.example.socialapp.services.interfaces.IAuthService;
import com.example.socialapp.services.interfaces.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDto dto) {
        User savedUser = userService.register(dto);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserDto dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
        public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // Aucun utilisateur connecté
        }

        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // ✅ Vérifie la validité du token
    @PostMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or missing token"));
        }

        return ResponseEntity.ok(Map.of(
            "username", userDetails.getUsername(),
            "authorities", userDetails.getAuthorities()
        ));
    }
}
