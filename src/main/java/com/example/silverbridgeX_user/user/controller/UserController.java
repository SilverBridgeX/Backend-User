package com.example.silverbridgeX_user.user.controller;

import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.user.converter.UserConverter;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.dto.JwtDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto.UserAddressReqDto;
import com.example.silverbridgeX_user.user.dto.UserRequestDto.UserNicknameReqDto;
import com.example.silverbridgeX_user.user.dto.UserResponseDto.OlderMyPageResDto;
import com.example.silverbridgeX_user.user.dto.UserResponseDto.ProtectorMyPageResDto;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그아웃", description = "로그아웃하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2001", description = "로그아웃 되었습니다."),
    })
    @DeleteMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, "refresh token 삭제 완료");
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2002", description = "토큰 재발급이 완료되었습니다."),
    })
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(
            HttpServletRequest request
    ) {
        JwtDto jwt = userService.reissue(request);
        return ApiResponse.onSuccess(SuccessCode.USER_REISSUE_SUCCESS, jwt);
    }

    @Operation(summary = "회원탈퇴", description = "회원 탈퇴하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2003", description = "회원탈퇴가 완료되었습니다."),
    })
    @DeleteMapping("/me")
    public ApiResponse<String> deleteUser(Authentication auth) {
        userService.deleteUser(auth.getName());
        return ApiResponse.onSuccess(SuccessCode.USER_DELETE_SUCCESS, "user entity 삭제 완료");
    }

    @Operation(summary = "닉네임 수정", description = "닉네임을 변경하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2004", description = "닉네임 변경이 완료되었습니다.")
    })
    @PostMapping(value = "/nickname")
    public ApiResponse<Boolean> nickname(
            @RequestBody UserNicknameReqDto nicknameReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.saveNickname(nicknameReqDto, user);
        return ApiResponse.onSuccess(SuccessCode.USER_NICKNAME_UPDATE_SUCCESS, true);
    }

    @Operation(summary = "주소 수정", description = "주소를 변경하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2005", description = "주소지 변경이 완료되었습니다.")
    })
    @PostMapping(value = "/address")
    public ApiResponse<Boolean> address(
            @RequestBody UserAddressReqDto addressReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.saveAddress(addressReqDto, user);
        return ApiResponse.onSuccess(SuccessCode.USER_ADDRESS_UPDATE_SUCCESS, true);
    }

    @Operation(summary = "노인 마이페이지 조회", description = "노인 마이페이지를 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2006", description = "노인 마이페이지 정보 조회가 완료되었습니다.")
    })
    @GetMapping(value = "/mypage/older")
    public ApiResponse<OlderMyPageResDto> mypageOlder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.validateOlder(user);
        return ApiResponse.onSuccess(SuccessCode.USER_MYPAGE_VIEW_SUCCESS, UserConverter.olderMyPageResDto(user));
    }

    @Operation(summary = "보호자 마이페이지 조회", description = "보호자 마이페이지를 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2006", description = "노인 마이페이지 정보 조회가 완료되었습니다.")
    })
    @GetMapping(value = "/mypage/protector")
    public ApiResponse<ProtectorMyPageResDto> mypageProtector(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User protector = userService.findByUserName(customUserDetails.getUsername());
        userService.validateProtector(protector);
        List<User> olders = protector.getOlders();

        return ApiResponse.onSuccess(SuccessCode.USER_MYPAGE_VIEW_SUCCESS,
                UserConverter.protectorMyPageResDto(protector, olders));
    }

    @Operation(summary = "보호자의 노인 연결", description = "보호자가 관리할 노인을 연결하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2007", description = "보호자의 노인 연결이 완료되었습니다.")
    })
    @PostMapping(value = "/protectors/older-links")
    public ApiResponse<Boolean> assignOlder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("olderKey") String olderKey
    ) {
        User protector = userService.findByUserName(customUserDetails.getUsername());
        userService.assignOlder(protector, olderKey);

        return ApiResponse.onSuccess(SuccessCode.USER_PROTECTOR_CONNECT_OLDER_SUCCESS, true);
    }

    @Operation(summary = "보호자의 노인 등록", description = "보호자가 관리할 노인을 등록하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2008", description = "보호자의 노인 등록이 완료되었습니다.")
    })
    @PostMapping(value = "/protectors/olders")
    public ApiResponse<String> registerOlder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserRequestDto.UserReqDto userReqDto
    ) throws Exception {
        User protector = userService.findByUserName(customUserDetails.getUsername());

        User older = userService.createUser(userReqDto);
        String key = userService.registerOlder(protector, older);

        return ApiResponse.onSuccess(SuccessCode.USER_PROTECTOR_REGISTER_OLDER_SUCCESS, key);
    }

}
