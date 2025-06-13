package com.example.silverbridgeX_user.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class RecommendationJobConfig {

    private final JobRepository jobRepository;

    /* 각 Step Bean 주입 */
    private final Step step1_CTR;
    private final Step step2_Rank;
    private final Step step3_UserEmb;
    private final Step step4_ActEmb;
    private final Step step5_Graph;
    private final Step step6_Prune;
    private final Step step7_FinalRec;

    /**
     * 매일 새벽 3시 실행 (Scheduler 에서 호출)
     */
    @Bean
    public Job recommendationJob() {
        return (Job) new JobBuilder("recommendationJob", jobRepository)
                .start(step1_CTR)
                .next(step2_Rank)
                .next(step3_UserEmb)
                .next(step4_ActEmb)
                .next(step5_Graph)
                .next(step6_Prune)
                .next(step7_FinalRec)
                .build();
    }
}


