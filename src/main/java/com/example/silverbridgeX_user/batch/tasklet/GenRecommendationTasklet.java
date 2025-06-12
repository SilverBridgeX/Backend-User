package com.example.silverbridgeX_user.batch.tasklet;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

// STEP 7. 최종 추천 리스트 생성 및 저장
@Component
@RequiredArgsConstructor
public class GenRecommendationTasklet implements Tasklet {
    private final RecommendActivityService recService;

    @Override
    public RepeatStatus execute(@NonNull StepContribution con, @NonNull ChunkContext ctx) {
        recService.generateAllUsersRecommendation();
        return RepeatStatus.FINISHED;
    }
}
