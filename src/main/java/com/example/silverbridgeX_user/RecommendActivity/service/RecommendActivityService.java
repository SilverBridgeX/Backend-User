package com.example.silverbridgeX_user.RecommendActivity.service;

import com.example.silverbridgeX_user.RecommendActivity.converter.RecommendActivityConverter;
import com.example.silverbridgeX_user.RecommendActivity.domain.ActivityLog;
import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.RecommendActivity.repository.RecommendActivityRepository;
import com.example.silverbridgeX_user.activity.converter.ActivityLogConverter;
import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.repository.ActivityLogRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRankingRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.domain.UserRole;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
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
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityRepository activityRepository;
    private final RecommendActivityRepository recommendActivityRepository;
    private final ActivityRankingRepository activityRankingRepository;
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

    public void execWrite(String cypher) {
        try (Session s = neo4jDriver.session()) {
            s.writeTransaction(tx -> {
                tx.run(cypher);
                return null;
            });
        }
    }

    /**
     * :PREFERRED 간선 베치 삽입
     */
    public void insertPreferredEdges(List<Map<String, Object>> rows) {
        try (var ses = neo4jDriver.session()) {
            ses.writeTransaction(tx -> {
                for (var r : rows) {
                    Long userId = ((Number) r.get("user_id")).longValue();
                    Long activityId = ((Number) r.get("activity_id")).longValue();
                    Double score = ((Number) r.get("score")).doubleValue();

                    tx.run(
                            "MATCH (u:User {id: $uid}), (a:Activity {id: $aid}) " +
                                    "MERGE (u)-[:PREFERRED {score: $score}]->(a)",
                            Map.of(
                                    "uid", userId,
                                    "aid", activityId,
                                    "score", score
                            )
                    );
                }
                return null;
            });
        }
    }


    /**
     * :SIMILAR 간선 베치 삽입
     */
    public void insertSimilarEdges(List<Map<String, Object>> rows) {
        try (var ses = neo4jDriver.session()) {
            ses.writeTransaction(tx -> {
                for (var r : rows) {
                    Long uid1 = ((Number) r.get("uid1")).longValue();
                    Long uid2 = ((Number) r.get("uid2")).longValue();
                    Double wSim = ((Number) r.get("w_sim")).doubleValue();

                    tx.run("""
                                MATCH (u1:User {id: $uid1}), (u2:User {id: $uid2})
                                MERGE (u1)-[rel:SIMILAR]-(u2)
                                SET rel.score = $w_sim
                            """, Map.of(
                            "uid1", uid1,
                            "uid2", uid2,
                            "w_sim", wSim
                    ));
                }
                return null;
            });
        }
    }

    /**
     * 하루에 한 번 실행 – prefer 14 + 인기 4 + 랜덤 2
     */
    public void generateAllUsersRecommendation() {

        // 0) 모든 사용자 조회 (PostgreSQL)
        List<User> users = userRepository.findAllUsersByRole(UserRole.OLDER);

        try (Session session = neo4jDriver.session()) {

            for (User user : users) {

                /* ---------- 1. PREFERRED 상위 10 & SIMILAR 사용자 SELECT 상위 N ---------- */
                List<Long> preferredIds = session.readTransaction(tx -> tx.run("""
                                    MATCH (u:User {id:$uid})-[r:PREFERRED]->(a:Activity)
                                    RETURN a.id AS aid
                                    ORDER BY r.score DESC
                                    LIMIT 10
                                """, Map.of("uid", user.getId()))
                        .list(r -> r.get("aid").asLong()));

                // SIMILAR 사용자들의 SELECT 상위(클릭 많은) 활동
                List<Long> similarTopIds = session.readTransaction(tx -> tx.run("""
                                    MATCH (u:User {id:$uid})-[:SIMILAR]-(s:User)-[:SELECTED]->(a:Activity)
                                    WITH a, count(*) AS freq
                                    ORDER BY freq DESC
                                    RETURN a.id AS aid
                                    LIMIT 14
                                """, Map.of("uid", user.getId()))
                        .list(r -> r.get("aid").asLong()));

                LinkedHashSet<Long> resultIds = new LinkedHashSet<>();
                resultIds.addAll(preferredIds);
                for (Long id : similarTopIds) {
                    if (resultIds.size() >= 14) {
                        break;
                    }
                    resultIds.add(id);
                }

                /* ---------- 2. 인기 랭킹 + 거리 필터 ---------- */
                List<Long> popularIds = activityRankingRepository.fetchTopByDistance(
                        Float.parseFloat(user.getLatitude()), Float.parseFloat(user.getLongitude()), 18, resultIds);

                for (Long id : popularIds) {
                    if (resultIds.size() >= 18) {
                        break;
                    }
                    resultIds.add(id);
                }

                /* ---------- 3. 무작위 2 ---------- */
                List<Long> randomIds = activityRepository.pickRandom(2, resultIds);
                resultIds.addAll(randomIds);


                /* ---------- 4. 추천 리스트 저장 ---------- */
                recommendActivityRepository.deleteByUser(user); // 기존 추천 삭제

                int order = 0;
                for (Long aid : resultIds) {
                    LocalDate today = LocalDate.now();
                    Activity activity = activityRepository.findById(aid)
                            .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
                    RecommendActivity recommendActivity
                            = RecommendActivityConverter.saveRecommendActivity(today, order++, user, activity);
                    recommendActivityRepository.save(recommendActivity);
                }
            }
        }
    }
}
