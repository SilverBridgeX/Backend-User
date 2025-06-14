package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.SaveTopRankTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SaveTopRankConfig {
    @Bean
    public Step step2_Rank(JobRepository repo, PlatformTransactionManager tx,
                           SaveTopRankTasklet tasklet) {
        return new StepBuilder("step2_Rank", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
