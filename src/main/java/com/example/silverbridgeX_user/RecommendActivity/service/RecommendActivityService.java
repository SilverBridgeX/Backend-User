package com.example.silverbridgeX_user.RecommendActivity.service;

import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.RecommendActivity.repository.RecommendActivityRepository;
import com.example.silverbridgeX_user.activity.converter.ActivityLogConverter;
import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityLog;
import com.example.silverbridgeX_user.activity.repository.ActivityLogRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendActivityService {
    private final ActivityLogRepository activityLogRepository;
    private final ActivityRepository activityRepository;
    private final RecommendActivityRepository recommendActivityRepository;
    private final Driver neo4jDriver;

    public List<RecommendActivity> getRecommendActivities(User user) {
        return recommendActivityRepository.findAllByUser(user);
    }

    public void handleActivitySelection(User user, Long activityId) {
        LocalDateTime now = LocalDateTime.now();
        // 1. 로그 저장
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
        ActivityLog activityLog = ActivityLogConverter.saveActivityLog(user, activity, now, "SELECT");
        activityLogRepository.save(activityLog);

        // 2. 간선 갱신
        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                // 기존 :PREFERRED 제거
                tx.run("""
                                MATCH (u:User {id: $uid})-[r:PREFERRED]->(a:Activity {id: $aid})
                                DELETE r
                                """,
                        Map.of("uid", user.getId(), "aid", activity.getId()));

                // :SELECTED 생성
                tx.run("""
                                MATCH (u:User {id: $uid}), (a:Activity {id: $aid})
                                MERGE (u)-[:SELECTED]->(a)
                                """,
                        Map.of("uid", user.getId(), "aid", activity.getId()));

                // 생성됐는지 확인
                var checkResult = tx.run("""
                                MATCH (u:User {id: $uid})-[r:SELECTED]->(a:Activity {id: $aid})
                                RETURN COUNT(r) > 0 AS exists
                                """,
                        Map.of("uid", user.getId(), "aid", activity.getId()));

                boolean exists = checkResult.single().get("exists").asBoolean();
                if (!exists) {
                    log.error("사용자 활동 SELECT 간선이 생기지 않았습니다.");
                }

                return null;
            });
        }
    }

    public void handleActivityView(User user, Long activityId) {
        LocalDateTime now = LocalDateTime.now();
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
        ActivityLog log = ActivityLogConverter.saveActivityLog(user, activity, now, "VIEW");
        activityLogRepository.save(log);
    }
}
