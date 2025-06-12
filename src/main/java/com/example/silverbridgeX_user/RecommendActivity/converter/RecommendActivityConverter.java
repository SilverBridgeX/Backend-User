package com.example.silverbridgeX_user.RecommendActivity.converter;

import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.RecommendActivity.dto.RecommendActivityResponseDto.RecommendActivityResDto;
import com.example.silverbridgeX_user.RecommendActivity.dto.RecommendActivityResponseDto.RecommendActivityResDtos;
import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendActivityConverter {
    public static RecommendActivity saveRecommendActivity(LocalDate date, Integer number, User user,
                                                          Activity activity) {
        return RecommendActivity.builder()
                .date(date)
                .user(user)
                .number(number)
                .activity(activity)
                .build();
    }

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
