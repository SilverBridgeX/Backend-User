package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.UpdateGraphTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class UpdateGraphConfig {
    @Bean
    public Step step5_Graph(JobRepository repo, PlatformTransactionManager tx,
                            UpdateGraphTasklet tasklet) {
        return new StepBuilder("step5_Graph", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
