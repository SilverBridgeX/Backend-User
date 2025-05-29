package com.example.silverbridgeX_user.matching.service;

import com.example.silverbridgeX_user.matching.converter.MatchingConverter;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.matching.repository.MatchingRepository;
import com.example.silverbridgeX_user.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;

    @Transactional
    public void saveMatchRequest(User user) {
        MatchRequest matchRequest = MatchingConverter.toMatchRequest(user);

        matchingRepository.save(matchRequest);
    }
}
