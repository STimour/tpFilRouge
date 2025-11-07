package com.example.socialapp.services.interfaces;

import com.example.socialapp.dto.UserDto;

public interface IAuthService {
    String login(UserDto dto);

    String refreshToken(String oldToken);
    
    void logout(String username);
}
