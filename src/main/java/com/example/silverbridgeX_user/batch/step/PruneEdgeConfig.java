package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.PruneEdgeTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PruneEdgeConfig {
    @Bean
    public Step step6_Prune(JobRepository repo, PlatformTransactionManager tx,
                            PruneEdgeTasklet tasklet) {
        return new StepBuilder("step6_Prune", repo)
                .tasklet(tasklet, tx)
                .build();
    }
}
