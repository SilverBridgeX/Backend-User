package com.example.silverbridgeX_user.batch.tasklet;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

// STEP 6. 불필요한 간선 제거
@Component
@RequiredArgsConstructor
public class PruneEdgeTasklet implements Tasklet {
    private final RecommendActivityService recommendActivityService;

    @Override
    public RepeatStatus execute(@NonNull StepContribution con, @NonNull ChunkContext ctx) {
        String del = """
                   MATCH ()-[r]->()
                    WHERE (type(r) = "PREFERRED" OR type(r) = "SIMILAR")
                      AND r.score < 0.85
                    DELETE r
                """;
        recommendActivityService.execWrite(del);
        return RepeatStatus.FINISHED;
    }
}
