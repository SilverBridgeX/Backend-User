package com.example.silverbridgeX_user.matching.converter;

import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.matching.domain.MatchStatus;
import com.example.silverbridgeX_user.user.domain.User;

public class MatchingConverter {

    public static MatchRequest toMatchRequest(User user) {
        return MatchRequest.builder()
                .status(MatchStatus.WAITING)
                .user(user)
                .build();
    }
}
