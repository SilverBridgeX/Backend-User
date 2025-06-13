package com.example.silverbridgeX_user.RecommendActivity.controller;

import com.example.silverbridgeX_user.RecommendActivity.converter.RecommendActivityConverter;
import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.RecommendActivity.dto.RecommendActivityResponseDto.RecommendActivityResDtos;
import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "활동 반환", description = "추천하는 활동 리스트를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ACTIVITY_2001", description = "추천하는 활동 리스를 반환 완료했습니다."),
    })
    @GetMapping("/recommend-activity")
    public ApiResponse<RecommendActivityResDtos> getRecommendActivities(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        List<RecommendActivity> recommendActivities = recommendActivityService.getRecommendActivities(user);
        return ApiResponse.onSuccess(SuccessCode.RECOMMEND_ACTIVITY_VIEW_LIST_SUCCESS,
                RecommendActivityConverter.recommendActivityResDtos(recommendActivities));
    }

    @Operation(summary = "활동 선택", description = "활동 선택 로그를 저장, selected 간선 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ACTIVITY_2002", description = "선택한 활동 로그와 selected 간선 저장 완료했습니다."),
    })
    @PostMapping("/select")
    public ApiResponse<String> select(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(name = "activityId") Long activityId
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        recommendActivityService.handleActivitySelection(user, activityId);
        return ApiResponse.onSuccess(SuccessCode.RECOMMEND_ACTIVITY_SELECT_LOG_SUCCESS, "선택 로그 저장 완료");
    }

    @Operation(summary = "활동 열람", description = "활동 열람 로그를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ACTIVITY_2003", description = "열람한 활동 로그를 저장 완료했습니다."),
    })
    @PostMapping("/view")
    public ApiResponse<String> view(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(name = "activityId") Long activityId
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        recommendActivityService.handleActivityView(user, activityId);
        return ApiResponse.onSuccess(SuccessCode.RECOMMEND_ACTIVITY_VIEW_LOG_SUCCESS, "열람 로그 저장 완료");
    }
}
