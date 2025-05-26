package com.example.silverbridgeX_user.RecommendActivity.controller;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "활동", description = "활동 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class RecommendActivityController {
    private final RecommendActivityService recommendActivityService;
    private final UserService userService;

    @Operation(summary = "활동 선택", description = "로그 저장, selected 간선 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ACTIVITY_2001", description = "선택한 활동 로그와 selected 간선 저장 완료했습니다."),
    })
    @DeleteMapping("/select")
    public ApiResponse<String> select(
            @RequestParam(name = "activityId") Long activityId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        recommendActivityService.handleActivitySelection(user, activityId);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, "로그 저장 완료");
    }

    @Operation(summary = "활동 열람", description = "로그 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ACTIVITY_2002", description = "열람한 활동 로그 저장 완료했습니다."),
    })
    @DeleteMapping("/view")
    public ApiResponse<String> view(
            @RequestParam(name = "activityId") Long activityId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        recommendActivityService.handleActivitySelection(user, activityId);

        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, "로그 저장 완료");
    }
}
