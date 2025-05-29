package com.example.silverbridgeX_user.RecommendActivity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class RecommendActivityResponseDto {

    @Schema(description = "RecommendActivityResDtos")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendActivityResDtos {
        @Schema(description = "추천 활동 리스트")
        private List<RecommendActivityResDto> recommendActivityResDtos;
    }

    @Schema(description = "RecommendActivityResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendActivityResDto {
        @Schema(description = "태그")
        private String tag;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "주소")
        private String address;

        @Schema(description = "기간/링크/전화번호")
        private String content;
    }

}