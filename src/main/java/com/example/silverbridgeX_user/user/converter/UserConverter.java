package com.example.silverbridgeX_user.user.converter;

import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;

public class UserConverter {
    public static User saveUser(UserRequestDto.UserReqDto userReqDto) {

        return User.builder()
                .email(userReqDto.getEmail())
                .username(userReqDto.getEmail())
                .nickname(userReqDto.getNickname())
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
