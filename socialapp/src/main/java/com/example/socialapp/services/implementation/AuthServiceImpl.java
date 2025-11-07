package com.example.socialapp.services.implementation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.socialapp.config.JwtService;
import com.example.socialapp.dto.UserDto;
import com.example.socialapp.entity.User;
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

        // 1️⃣ Vérifie les identifiants
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // 2️⃣ Récupère l'utilisateur
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 3️⃣ Révoque tous les anciens tokens actifs
        List<Token> validTokens = tokenRepository.findAllByUserIdAndRevokedFalse(user.getId());
        validTokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(validTokens);

        // 4️⃣ Génère un nouveau JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        String jwt = jwtService.generateToken(claims, user.getUsername());

        // 5️⃣ Enregistre le nouveau token
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

        // 6️⃣ Retourne le JWT au client
        return jwt;
    }



    @Override
    public String refreshToken(String oldToken) {
        // TODO 
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }

    @Override
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Token> validTokens = tokenRepository.findAllByUserIdAndRevokedFalse(user.getId());
        validTokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(validTokens);
    }


}
