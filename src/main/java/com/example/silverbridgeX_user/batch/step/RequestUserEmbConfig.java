package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.RequestUserEmbTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// STEP 3
@Configuration
public class RequestUserEmbConfig {
    @Bean
    public Step step3_UserEmb(JobRepository repo, PlatformTransactionManager tx,
                              RequestUserEmbTasklet tasklet) {
        return new StepBuilder("step3_UserEmb", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
