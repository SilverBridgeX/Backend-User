package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.RequestActEmbTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// STEP 4.
@Configuration
public class RequestActEmbConfig {
    @Bean
    public Step step4_ActEmb(JobRepository repo, PlatformTransactionManager tx,
                             RequestActEmbTasklet tasklet) {
        return new StepBuilder("step4_ActEmb", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
