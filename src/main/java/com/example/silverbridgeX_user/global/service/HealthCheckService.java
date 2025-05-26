package com.example.silverbridgeX_user.global.service;

import com.example.silverbridgeX_user.global.dto.HealthCheckDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final WebClient webClient;

    @Value("${chat.server.url}")
    private String chatServerUrl;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public String checkChatServerHealth() {
        HealthCheckDTO.MessageDTO response = webClient.get()
                .uri(chatServerUrl + "/health")
                .retrieve()
                .bodyToMono(HealthCheckDTO.MessageDTO.class)
                .block();

        String message = response.getMessage();
        return message;
    }

    public String checkAIServerHealth() {
        HealthCheckDTO.MessageDTO response = webClient.get()
                .uri(aiServerUrl + "/health")
                .retrieve()
                .bodyToMono(HealthCheckDTO.MessageDTO.class)
                .block();

        String message = response.getMessage();
        return message;
    }
}
