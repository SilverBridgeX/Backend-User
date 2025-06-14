package com.example.silverbridgeX_user.batch.tasklet;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.silverbridgeX_user.RecommendActivity.service.RecommendActivityService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UpdateGraphTaskletTest {
    @Test
    void execute_updatesGraphEdges() throws Exception {
        NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
        RecommendActivityService svc = mock(RecommendActivityService.class);

        when(jdbc.queryForList(anyString(), anyMap())).thenReturn(List.of(Map.of("dummy", "value")));

        UpdateGraphTasklet tasklet = new UpdateGraphTasklet(jdbc, svc);
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

        verify(svc).insertPreferredEdges(anyList());
        verify(svc).insertSimilarEdges(anyList());
    }
}
