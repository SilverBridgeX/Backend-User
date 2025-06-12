package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.GenRecommendationTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class GenRecommendationConfig {
    @Bean
    public Step step7_FinalRec(JobRepository repo, PlatformTransactionManager tx,
                               GenRecommendationTasklet tasklet) {
        return new StepBuilder("step7_FinalRec", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
