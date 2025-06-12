package com.example.silverbridgeX_user.batch.tasklet;

import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import com.example.silverbridgeX_user.user.service.UserService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

// STEP 3. 사용자별 갱신된 keywords로 prefEmb 구해서 갱신
@Component
@RequiredArgsConstructor
public class RequestUserEmbTasklet implements Tasklet {

    private final UserRepository userRepo;
    private final EmbeddingClient embeddingClient;
    private final UserService userService;

    @Override
    public RepeatStatus execute(@NonNull StepContribution con, @NonNull ChunkContext ctx) throws Exception {

        // 사용자별 선호 키워드 업데이트
        userService.updatePreferredKeywords();

        List<User> users = userRepo.findAll();
        for (User user : users) {

            List<String> keywords = user.getPreferredKeywords();
            if (keywords == null || keywords.isEmpty()) {
                continue;
            }

            // 키워드들 각각을 임베딩하고, max pooling 값 저장
            String vectorLiteral = embeddingClient.getMaxPooledEmbeddingLiteral(keywords);

            try {
                // PostgreSQL vector 타입으로 인식시키기 위해 문자열 그대로 저장
                userRepo.updatePreferredEmbedding(user.getId(), vectorLiteral);
                System.out.println("success");
            } catch (Exception e) {
                System.err.println("유저 임베딩 실패: " + user.getId());
                e.printStackTrace();
            }
        }

        return RepeatStatus.FINISHED;
    }

}

