package com.example.silverbridgeX_user.global.controller;

import com.example.silverbridgeX_user.global.converter.HealthCheckConverter;
import com.example.silverbridgeX_user.global.dto.HealthCheckDTO;
import com.example.silverbridgeX_user.global.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final HealthCheckService healthCheckService;

    @GetMapping("/health")
    public HealthCheckDTO.MessageDTO healthCheck() {
        return HealthCheckConverter.toHealthCheckDTO("User server is healthy");
    }

    @GetMapping("/check/chat-server")
    public HealthCheckDTO.MessageDTO checkChatServer() {
        String message = healthCheckService.checkChatServerHealth();
        return HealthCheckConverter.toHealthCheckDTO(message);
    }

    @GetMapping("/check/ai-server")
    public HealthCheckDTO.MessageDTO checkAIServer() {
        String message = healthCheckService.checkAIServerHealth();
        return HealthCheckConverter.toHealthCheckDTO(message);
    }
}
