package com.example.silverbridgeX_user.matching.converter;

import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.matching.domain.MatchStatus;
import com.example.silverbridgeX_user.matching.dto.MatchingDto;
import com.example.silverbridgeX_user.user.domain.User;

import java.util.List;

public class MatchingConverter {

    public static MatchRequest toMatchRequest(User user) {
        return MatchRequest.builder()
                .status(MatchStatus.WAITING)
                .user(user)
                .build();
    }

    public static MatchingDto.Request toMatchingDtoRequest(User a, User b) {
        MatchingDto.Participant participantA = MatchingDto.Participant.builder()
                .id(a.getId())
                .name(a.getNickname())
                .build();
        MatchingDto.Participant participantB = MatchingDto.Participant.builder()
                .id(b.getId())
                .name(b.getNickname())
                .build();

        return MatchingDto.Request.builder()
                .participants(List.of(participantA, participantB))
                .build();
    }
}
