package com.example.silverbridgeX_user.batch.tasklet

import com.example.silverbridgeX_user.global.util.EmbeddingClient
import com.example.silverbridgeX_user.user.domain.User
import com.example.silverbridgeX_user.user.repository.UserRepository
import com.example.silverbridgeX_user.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus

class RequestUserEmbTaskletTestKt {

    private val userRepository: UserRepository = mock()
    private val embeddingClient: EmbeddingClient = mock()
    private val userService: UserService = mock()
    private val contribution: StepContribution = mock()
    private val chunkContext: ChunkContext = mock()

    private lateinit var tasklet: RequestUserEmbTasklet

    @BeforeEach
    fun setUp() {
        tasklet = RequestUserEmbTasklet(userRepository, embeddingClient, userService)
    }

    @Test
    fun `사용자의 키워드가 존재하면 임베딩이 갱신된다`() {
        // Given
        val user = mock<User> {
            on { id } doReturn 1L
            on { preferredKeywords } doReturn listOf("문화", "자연")
        }

        whenever(userRepository.findAll()).thenReturn(listOf(user))
        whenever(embeddingClient.getMaxPooledEmbeddingLiteral(any()))
                .thenReturn("'[0.1, 0.2, 0.3]'")

        // When
        val result = tasklet.execute(contribution, chunkContext)

        // Then
        verify(userService).updatePreferredKeywords()
        verify(userRepository).updatePreferredEmbedding(eq(1L), any())
        assertThat(result).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `사용자의 키워드가 없으면 임베딩 갱신을 건너뛴다`() {
        // Given
        val user = mock<User> {
            on { preferredKeywords } doReturn emptyList()
        }

        whenever(userRepository.findAll()).thenReturn(listOf(user))

        // When
        val result = tasklet.execute(contribution, chunkContext)

        // Then
        verify(userRepository, never()).updatePreferredEmbedding(any(), any())
        assertThat(result).isEqualTo(RepeatStatus.FINISHED)
    }

    @Test
    fun `임베딩 갱신 중 예외가 발생해도 정상 종료된다`() {
        // Given
        val user = mock<User> {
            on { id } doReturn 99L
            on { preferredKeywords } doReturn listOf("키워드")
        }

        whenever(userRepository.findAll()).thenReturn(listOf(user))
        whenever(embeddingClient.getMaxPooledEmbeddingLiteral(any()))
                .thenReturn("'[0.1, 0.2]'")
        doThrow(RuntimeException("DB Error"))
                .whenever(userRepository).updatePreferredEmbedding(eq(99L), any())

        // When
        val result = tasklet.execute(contribution, chunkContext)

        // Then
        verify(userRepository).updatePreferredEmbedding(eq(99L), any())
        assertThat(result).isEqualTo(RepeatStatus.FINISHED)
    }
}
