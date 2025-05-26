package com.example.silverbridgeX_user.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserResponseDto {

    @Schema(description = "UserMyPageResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserMyPageResDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "프로필 이미지")
        private String profileImage;
    }

}

