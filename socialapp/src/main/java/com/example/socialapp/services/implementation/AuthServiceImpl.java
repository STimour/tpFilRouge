package com.example.socialapp.services.implementation;

import java.time.LocalDateTime;                  // ✅ manquant
import java.util.HashMap;
import java.util.List;                           // ✅ manquant
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.socialapp.config.JwtService;
import com.example.socialapp.dto.UserDto;
import com.example.socialapp.entity.Token;
import com.example.socialapp.entity.User;
import com.example.socialapp.repository.TokenRepository;
import com.example.socialapp.repository.UserRepository;
import com.example.socialapp.services.interfaces.IAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    @Override
    public String login(UserDto dto) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Token> validTokens = tokenRepository.findAllByUserIdAndRevokedFalse(user.getId());
        validTokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(validTokens);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        String jwt = jwtService.generateToken(claims, user.getUsername());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusHours(24);

        Token tokenEntity = Token.builder()
                .token(jwt)
                .createdAt(now)
                .expiresAt(expiration)
                .revoked(false)
                .user(user)
                .build();

        tokenRepository.save(tokenEntity);
        return jwt;
    }

    @Override
    public String refreshToken(String oldToken) {
        throw new RuntimeException("Unimplemented method 'refreshToken'");
    }

    @Override
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));  // ✅ ou RuntimeException

        List<Token> validTokens = tokenRepository.findAllByUserIdAndRevokedFalse(user.getId());
        validTokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(validTokens);
    }
}
