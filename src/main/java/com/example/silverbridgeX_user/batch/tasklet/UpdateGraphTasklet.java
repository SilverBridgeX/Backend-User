package com.example.silverbridgeX_user.batch.tasklet;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

// STEP 5. 임베딩 기반 그래프 갱신
@Component
@RequiredArgsConstructor
public class UpdateGraphTasklet implements Tasklet {

    private final NamedParameterJdbcTemplate jdbc;          // PostgreSQL 접근용
    private final RecommendActivityService recommendSvc;    // Neo4j 간선 INSERT 담당

    @Override
    public RepeatStatus execute(StepContribution con, ChunkContext ctx) {

        /*************** 1) 사용자 → 활동 :PREFERRED (Top-2) ****************/
        String preferredSql = """
                    WITH cand AS (
                      SELECT
                        u.id      AS user_id,
                        a.id      AS activity_id,
                        1 - ( 0.3 * cosine_distance(u.preferred_embedding, a.description_embedding)
                            + 0.7 * cosine_distance(u.activity_embedding_avg, a.description_embedding) ) AS score,
                        row_number() OVER (PARTITION BY u.id
                                           ORDER BY 1 - ( 0.3 * cosine_distance(u.preferred_embedding, a.description_embedding)
                                                         + 0.7 * cosine_distance(u.activity_embedding_avg, a.description_embedding) ) DESC) AS rk
                      FROM member   u
                      CROSS JOIN activity a
                      WHERE u.preferred_embedding       IS NOT NULL
                        AND u.activity_embedding_avg    IS NOT NULL
                        AND a.description_embedding     IS NOT NULL
                        -- 거리 < 10 km
                        AND earth_distance(
                              ll_to_earth(u.latitude  ::float, u.longitude ::float),
                              ll_to_earth(a.latitude  ::float, a.longitude ::float)
                            ) < 10000
                        -- 이미 SELECTED 인 활동 제외
                        AND NOT EXISTS (
                            SELECT 1 FROM activity_log al
                            WHERE al.user_id = u.id
                              AND al.activity_id = a.id
                              AND al.action_type = 1
                        )
                    )
                    SELECT user_id, activity_id, score
                    FROM cand
                    WHERE score >= 0.5
                      AND rk <= 2;
                """;

        List<Map<String, Object>> prefRows = jdbc.queryForList(preferredSql, Map.of());
        recommendSvc.insertPreferredEdges(prefRows);   // Neo4j MERGE (u)-[:PREFERRED]->(a)

        /*************** 2) 사용자 ↔ 사용자 :SIMILAR ****************/
        String similarSql = """
                    WITH pair AS (
                      SELECT
                        u1.id  AS uid1,
                        u2.id  AS uid2,
                        1 - cosine_distance(u1.preferred_embedding,        u2.preferred_embedding       ) AS pref_sim,
                        1 - cosine_distance(u1.activity_embedding_avg,     u2.activity_embedding_avg    ) AS act_sim,
                        (SELECT COUNT(*) FROM activity_log al WHERE al.user_id = u1.id AND al.action_type = 1) AS cnt1,
                        (SELECT COUNT(*) FROM activity_log al WHERE al.user_id = u2.id AND al.action_type = 1) AS cnt2
                      FROM member u1
                      JOIN member u2 ON u1.id < u2.id
                      WHERE u1.preferred_embedding    IS NOT NULL
                        AND u2.preferred_embedding    IS NOT NULL
                    )
                    SELECT
                      uid1, uid2,
                      CASE
                         WHEN cnt1 = 0 AND cnt2 > 0 THEN pref_sim               -- cold-start
                         ELSE 0.7 * pref_sim + 0.3 * act_sim
                      END AS w_sim
                    FROM pair
                    WHERE
                      (CASE
                         WHEN cnt1 = 0 AND cnt2 > 0 THEN pref_sim
                         ELSE 0.7 * pref_sim + 0.3 * act_sim
                      END) >= 0.5;
                """;

        List<Map<String, Object>> simRows = jdbc.queryForList(similarSql, Map.of());
        recommendSvc.insertSimilarEdges(simRows);     // Neo4j MERGE (u1)-[:SIMILAR]-(u2)

        return RepeatStatus.FINISHED;
    }
}