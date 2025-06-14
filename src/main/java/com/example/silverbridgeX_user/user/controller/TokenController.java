package com.example.silverbridgeX_user.user.controller;

import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.user.converter.UserConverter;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "토큰", description = "access token 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final UserService userService;

    @Operation(summary = "토큰 반환", description = "노인/보호자 소셜 로그인 후, 프론트에게 유저 정보 받아 토큰 반환하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2011", description = "회원가입 & 로그인 성공"),
    })
    @PostMapping("/generate/social")
    public ApiResponse<JwtDto> tokenToFront(
            @RequestBody UserRequestDto.UserReqDto userReqDto
    ) throws Exception {
        Boolean isMember = userService.checkMemberByEmail(userReqDto.getEmail());

        String accessToken = "";
        String refreshToken = "";

        String signIn = "wasUser";

        if (isMember) {
            User user = userService.findByEmail(userReqDto.getEmail());

            JwtDto jwt = userService.jwtMakeSave(user.getUsername());
            accessToken = jwt.getAccessToken();
            refreshToken = jwt.getRefreshToken();

        } else {
            User user = userService.createUser(userReqDto);

            JwtDto jwt = userService.jwtMakeSave(user.getUsername());
            accessToken = jwt.getAccessToken();
            refreshToken = jwt.getRefreshToken();

            signIn = "newUser";
        }

        return ApiResponse.onSuccess(SuccessCode.USER_SOCIAL_LOGIN_SUCCESS,
                UserConverter.jwtDto(accessToken, refreshToken, signIn));
    }

    @Operation(summary = "토큰 반환", description = "노인의 key 로그인 후, 프론트에게 유저 정보 받아 토큰 반환하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2012", description = "회원가입 & 로그인 성공"),
    })
    @PostMapping("/generate/key")
    public ApiResponse<JwtDto> tokenToFront(
            @RequestParam String key
    ) {
        User user = userService.findByUserName(key);

        JwtDto jwt = userService.jwtMakeSave(user.getUsername());
        String accessToken = jwt.getAccessToken();
        String refreshToken = jwt.getRefreshToken();

        return ApiResponse.onSuccess(SuccessCode.USER_KEY_LOGIN_SUCCESS,
                UserConverter.jwtDto(accessToken, refreshToken, "wasUser"));
    }
}
