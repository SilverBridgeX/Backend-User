package com.example.silverbridgeX_user.batch.tasklet;

import com.example.silverbridgeX_user.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

// STEP 4. 사용자별 SELECT로 연결된 활동 임베딩들의 평균값 계산 -> actEmb 계산
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestActEmbTasklet implements Tasklet {

    private final UserRepository userRepository;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {

        int updatedCount = userRepository.updateActivityEmbeddingAvgForUsers();
        log.info("사용자 활동 임베딩 평균 업데이트 완료: " + updatedCount + " rows affected");
        return RepeatStatus.FINISHED;
    }
}
