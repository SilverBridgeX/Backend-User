package com.example.silverbridgeX_user.batch.tasklet;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

public class PruneEdgeTaskletTest {
    @Test
    void execute_deletesLowScoreEdges() throws Exception {
        // Given
        RecommendActivityService mockService = mock(RecommendActivityService.class);
        PruneEdgeTasklet tasklet = new PruneEdgeTasklet(mockService);

        // When
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

        // Then
        verify(mockService).execWrite(anyString());
    }
}
