package com.example.silverbridgeX_user.batch.tasklet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import com.example.silverbridgeX_user.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class RequestUserEmbTaskletTest {

    @InjectMocks
    private RequestUserEmbTasklet tasklet;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmbeddingClient embeddingClient;

    @Mock
    private UserService userService;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_successfully_updates_embeddings() throws Exception {
        // Given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getPreferredKeywords()).thenReturn(List.of("문화", "자연"));

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(embeddingClient.getMaxPooledEmbeddingLiteral(anyList()))
                .thenReturn("'[0.1, 0.2, 0.3]'");

        // When
        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        // Then
        verify(userService).updatePreferredKeywords();
        verify(userRepository).updatePreferredEmbedding(eq(1L), anyString());
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
    }

    @Test
    void execute_skips_user_with_no_keywords() throws Exception {
        // Given
        User user = mock(User.class);
        when(user.getPreferredKeywords()).thenReturn(List.of());

        when(userRepository.findAll()).thenReturn(List.of(user));

        // When
        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        // Then
        verify(userRepository, never()).updatePreferredEmbedding(anyLong(), anyString());
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
    }

    @Test
    void execute_handles_exception_during_embedding() throws Exception {
        // Given
        User user = mock(User.class);
        when(user.getId()).thenReturn(99L);
        when(user.getPreferredKeywords()).thenReturn(List.of("키워드"));

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(embeddingClient.getMaxPooledEmbeddingLiteral(anyList()))
                .thenReturn("'[0.1, 0.2]'");
        doThrow(new RuntimeException("DB Error"))
                .when(userRepository).updatePreferredEmbedding(anyLong(), anyString());

        // When
        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        // Then
        verify(userRepository).updatePreferredEmbedding(eq(99L), anyString());
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
    }
}
