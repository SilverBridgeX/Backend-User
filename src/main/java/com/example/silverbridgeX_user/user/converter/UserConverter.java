package com.example.silverbridgeX_user.user.converter;

import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;

public class UserConverter {
    public static User saveUser(UserRequestDto.UserReqDto userReqDto, String key) {

        return User.builder()
                .role(userReqDto.getRole())
                .email(userReqDto.getEmail())
                .username(key)
                .nickname(userReqDto.getNickname())
                .streetAddress(userReqDto.getStreetAddress())
                .build();
    }

    public static JwtDto jwtDto(String access, String refresh, String signIn) {
        return JwtDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .signIn(signIn)
                .build();
    }


}
