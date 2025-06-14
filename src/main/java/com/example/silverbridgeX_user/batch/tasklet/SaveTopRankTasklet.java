package com.example.silverbridgeX_user.batch.tasklet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// STEP 2. CTR 높은 순으로 랭킹 매기기
@Component
@RequiredArgsConstructor
public class SaveTopRankTasklet implements Tasklet {

    private final JdbcTemplate jdbc;

    @Override
    public RepeatStatus execute(@NonNull StepContribution con, @NonNull ChunkContext ctx) {
        jdbc.update("DELETE FROM activity_ranking");
        jdbc.update("""
                    INSERT INTO activity_ranking (activity_id, rank)
                    SELECT id, rank
                    FROM (
                        SELECT id,
                               RANK() OVER (ORDER BY ctr DESC) AS rank
                        FROM activity
                        WHERE ctr > 0
                    ) sub
                    ORDER BY rank
                    LIMIT 50
                """);
        return RepeatStatus.FINISHED;
    }
}

