package com.example.silverbridgeX_user.batch.tasklet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// STEP 1. CTR 계산, 저장
@Component
@RequiredArgsConstructor
public class CalculateCtrTasklet implements Tasklet {

    private final JdbcTemplate jdbc;

    @Override
    public RepeatStatus execute(@NonNull StepContribution con, @NonNull ChunkContext ctx) {
        String sql = """
                    UPDATE activity a SET
                      click_num = sub.click,
                      impression_num = sub.impression,
                      ctr = CASE WHEN sub.impression > 0
                                 THEN sub.click::float / sub.impression
                                 ELSE 0 END
                    FROM (
                      SELECT activity_id,
                             COUNT(*) FILTER (WHERE action_type = 1) AS click,
                             COUNT(*) FILTER (WHERE action_type = 0) AS impression
                      FROM activity_log
                      GROUP BY activity_id
                    ) sub
                    WHERE a.id = sub.activity_id
                """;

        jdbc.update(sql);
        return RepeatStatus.FINISHED;
    }
}
