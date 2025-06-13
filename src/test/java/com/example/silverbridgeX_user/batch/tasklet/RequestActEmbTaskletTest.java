package com.example.silverbridgeX_user.batch.tasklet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.silverbridgeX_user.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

public class RequestActEmbTaskletTest {
    @Test
    void execute_updatesEmbeddingAvg() throws Exception {
        // Given
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.updateActivityEmbeddingAvgForUsers()).thenReturn(3);

        RequestActEmbTasklet tasklet = new RequestActEmbTasklet(mockRepo);

        // When
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

        // Then
        verify(mockRepo, times(1)).updateActivityEmbeddingAvgForUsers();
    }

}
