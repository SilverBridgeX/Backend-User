package com.example.silverbridgeX_user.matching.controller;

import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.matching.service.MatchingService;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/match/requests")
public class MatchingController {

    private final UserService userService;
    private final MatchingService matchingService;

    @Operation(summary = "매치 신청", description = "매치를 신청받는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MATCH_REQUEST_2001", description = "매치 신청이 완료되었습니다.")
    })
    @PostMapping("")
    public ApiResponse<Boolean> matchRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        matchingService.saveMatchRequest(user);

        return ApiResponse.onSuccess(SuccessCode.MATCH_REQUEST_SUCCESS, true);
    }

}
