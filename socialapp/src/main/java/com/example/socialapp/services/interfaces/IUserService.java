package com.example.socialapp.services.interfaces;

import com.example.socialapp.dto.UserDto;
import com.example.socialapp.entity.User;

public interface IUserService {
    boolean register(UserDto dto);
}
