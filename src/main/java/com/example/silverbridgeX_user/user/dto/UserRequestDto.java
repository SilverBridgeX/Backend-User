package com.example.silverbridgeX_user.user.dto;

import com.example.silverbridgeX_user.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "UserSigInReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserSigInReqDto {
        @Schema(description = "역할(OLDER/GUARDIAN)")
        private UserRole role;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "도로명 주소")
        private String streetAddress;

    }

    @Schema(description = "UserLoginReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginReqDto {
        @Schema(description = "역할(OLDER/GUARDIAN)")
        private UserRole role;

        @Schema(description = "이메일")
        private String email;
    }

    @Schema(description = "UserNicknameReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserNicknameReqDto {

        @Schema(description = "닉네임")
        private String nickname;

    }

    @Schema(description = "UserAddressReqDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAddressReqDto {

        @Schema(description = "도로명 주소")
        private String streetAddress;

    }

    @Schema(description = "UserPreferenceDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPreferenceDto {
        private Long userId;
        private List<String> preferences;
    }

}
