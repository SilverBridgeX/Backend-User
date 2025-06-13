package com.example.silverbridgeX_user.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityInserter implements ApplicationRunner {
    private final ActivityService activityService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("🌟 ApplicationRunner로 실행 중");
//        activityService.fetchAndSaveFestivalData();
//        activityService.fetchAndSaveTourSpotData();
//        activityService.fetchAndSaveCoordinate();
//        activityService.insertActivitiesNeo4j();
//        System.out.println("insert finish");
    }
}
