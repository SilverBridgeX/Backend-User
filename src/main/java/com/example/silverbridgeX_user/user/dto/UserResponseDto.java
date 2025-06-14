package com.example.silverbridgeX_user.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserResponseDto {

    @Schema(description = "OlderMyPageResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OlderMyPageResDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "key")
        private String key;

        @Schema(description = "address")
        private String address;

        @Schema(description = "email")
        private String email;
    }

    @Schema(description = "ProtectorMyPageResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProtectorMyPageResDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "key")
        private String key;

        @Schema(description = "address")
        private String address;

        @Schema(description = "email")
        private String email;
    }

}

