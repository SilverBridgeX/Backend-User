package com.example.silverbridgeX_user.batch.step;

import com.example.silverbridgeX_user.batch.tasklet.CalculateCtrTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CalculateCtrConfig {
    @Bean
    public Step step1_CTR(JobRepository jobRepository,
                          PlatformTransactionManager txManager,
                          CalculateCtrTasklet tasklet) {
        return new StepBuilder("step1_CTR", jobRepository)
                .tasklet(tasklet, txManager)
                .build();
    }
}
