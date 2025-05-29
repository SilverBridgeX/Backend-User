package com.example.silverbridgeX_user.RecommendActivity.converter;

import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.RecommendActivity.dto.RecommendActivityResponseDto.RecommendActivityResDto;
import com.example.silverbridgeX_user.RecommendActivity.dto.RecommendActivityResponseDto.RecommendActivityResDtos;
import com.example.silverbridgeX_user.activity.domain.Activity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecommendActivityConverter {
    public static RecommendActivityResDto recommendActivityResDto(RecommendActivity recommendActivity) {
        Activity activity = recommendActivity.getActivity();
        String address =
                activity.getStreetAddress().isEmpty() ? activity.getLotNumberAddress() : activity.getStreetAddress();
        String content = activity.getEndDate().equals("2999-12-31")
                ? (activity.getHomepageUrl() != null && !activity.getHomepageUrl().isBlank()
                ? activity.getHomepageUrl()
                : activity.getPhoneNumber())
                : activity.getStartDate() + " ~ " + activity.getEndDate();

        return RecommendActivityResDto.builder()
                .tag(String.valueOf(activity.getActivityType()))
                .name(activity.getName())
                .address(address)
                .content(content)
                .build();
    }

    public static RecommendActivityResDtos recommendActivityResDtos(List<RecommendActivity> activities) {

        List<RecommendActivityResDto> dtoList = activities.stream()
                .map(RecommendActivityConverter::recommendActivityResDto)
                .collect(Collectors.toList());

        return RecommendActivityResDtos.builder()
                .recommendActivityResDtos(dtoList)
                .build();
    }

}
