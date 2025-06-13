package com.example.silverbridgeX_user.batch.tasklet;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class SaveTopRankTaskletTest {
    @Test
    void execute_ranksAndInsertsSuccessfully() throws Exception {
        // Given
        JdbcTemplate mockJdbc = mock(JdbcTemplate.class);
        SaveTopRankTasklet tasklet = new SaveTopRankTasklet(mockJdbc);

        StepContribution mockCon = mock(StepContribution.class);
        ChunkContext mockCtx = mock(ChunkContext.class);

        // When
        tasklet.execute(mockCon, mockCtx);

        // Then
        verify(mockJdbc, times(1)).update("DELETE FROM activity_ranking");
        verify(mockJdbc, times(2)).update(anyString());
    }
}
