package com.example.silverbridgeX_user.matching.scheduler;

import com.example.silverbridgeX_user.matching.service.MatchingService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchingService matchingService;

    @Scheduled(cron = "0 9,19,29,39,49,59 * * * *")
    public void executeMatching() {
        System.out.println("매칭 알고리즘 실행됨: " + LocalDateTime.now());
        matchingService.executeMatchingAlgorithm();
    }
}
