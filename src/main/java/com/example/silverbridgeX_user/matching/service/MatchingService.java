package com.example.silverbridgeX_user.matching.service;

import com.example.silverbridgeX_user.matching.algorithm.AdjacencyGraphBuilder;
import com.example.silverbridgeX_user.matching.algorithm.Matcher;
import com.example.silverbridgeX_user.matching.converter.MatchingConverter;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.matching.domain.MatchStatus;
import com.example.silverbridgeX_user.matching.dto.MatchingDto;
import com.example.silverbridgeX_user.matching.repository.MatchingRepository;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final WebClient webClient;

    @Value("${chat.server.url}")
    private String chatServerUrl;

    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveMatchRequest(User user) {
        MatchRequest matchRequest = MatchingConverter.toMatchRequest(user);

        matchingRepository.save(matchRequest);
    }


    @Transactional
    public void executeMatchingAlgorithm() {
        List<MatchRequest> matchRequests = matchingRepository.findAllWithUserByStatus(MatchStatus.WAITING); // size: n

        List<Integer>[] adj = AdjacencyGraphBuilder.buildAdjacencyGraph(matchRequests); // size: n + 1


        Matcher matcher = new Matcher(adj); // size: n + 1
        int matchCount = matcher.run();
        System.out.println(matchCount);
        Integer[] matching = matcher.getMatching();

        List<MatchingDto.Request> requests = new ArrayList<>();
        for (int i = 1; i < matching.length; i++) {
            if (matching[i] != null && i < matching[i]) {
                MatchingDto.Request request = MatchingConverter.toMatchingDtoRequest(
                        matchRequests.get(i - 1).getUser(),
                        matchRequests.get(matching[i] - 1).getUser()
                );
                requests.add(request);

                matchRequests.get(i - 1).updateStatus(MatchStatus.MATCHED);
                matchRequests.get(matching[i] - 1).updateStatus(MatchStatus.MATCHED);
            }
        }

        webClient.post()
                .uri(chatServerUrl + "/room/all")
                .bodyValue(requests)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }


}
