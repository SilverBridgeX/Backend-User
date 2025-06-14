package com.example.silverbridgeX_user.batch.tasklet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

public class GenRecommendationTaskletTest {
    @Test
    void execute_generatesRecommendation() throws Exception {
        // Given
        RecommendActivityService mockService = mock(RecommendActivityService.class);
        GenRecommendationTasklet tasklet = new GenRecommendationTasklet(mockService);

        // When
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

        // Then
        verify(mockService, times(1)).generateAllUsersRecommendation();
    }
}
