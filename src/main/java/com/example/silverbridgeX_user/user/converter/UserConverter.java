package com.example.silverbridgeX_user.user.converter;

import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;
import com.example.silverbridgeX_user.user.dto.UserResponseDto.OlderInfoDto;
import com.example.silverbridgeX_user.user.dto.UserResponseDto.OlderMyPageResDto;
import com.example.silverbridgeX_user.user.dto.UserResponseDto.ProtectorMyPageResDto;
import java.util.List;

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

    public static OlderMyPageResDto olderMyPageResDto(User user) {
        return OlderMyPageResDto.builder()
                .key(user.getUsername())
                .nickname(user.getNickname())
                .address(user.getStreetAddress())
                .email(user.getEmail())
                .build();
    }

    public static ProtectorMyPageResDto protectorMyPageResDto(User user, List<User> olders) {
        List<OlderInfoDto> olderInfoDtos = olders.stream()
                .map(older -> OlderInfoDto.builder()
                        .nickname(older.getNickname())
                        .key(older.getUsername())
                        .build())
                .toList();

        return ProtectorMyPageResDto.builder()
                .key(user.getUsername())
                .nickname(user.getNickname())
                .address(user.getStreetAddress())
                .email(user.getEmail())
                .olderInfoDtos(olderInfoDtos)
                .build();
    }
    
}
