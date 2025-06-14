package com.example.silverbridgeX_user.matching.scheduler;

import com.example.silverbridgeX_user.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchingService matchingService;

    @Scheduled(cron = "0 18,38,58 * * * *")
    public void executeMatching() {
        System.out.println("매칭 알고리즘 실행됨: " + LocalDateTime.now());
        matchingService.executeMatchingAlgorithm();
    }
}
