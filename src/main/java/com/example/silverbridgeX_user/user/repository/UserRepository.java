package com.example.silverbridgeX_user.user.repository;

import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.domain.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 1. 사용자 계정이름으로 사용자 정보를 회수하는 기능
    Optional<User> findByUsername(String username);

    // 2. 사용자 이메일으로 사용자 정보를 회수하는 기능
    Optional<User> findByEmail(String email);

    // 3. 사용자 계정이름을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByUsername(String username);

    // 4. 사용자 이메일을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByEmail(String email);

    // 5. 닉네임이 사용중인지 판단하는 기능
    boolean existsByNickname(String nickname);

    List<User> findAllUsersByRole(UserRole role);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member SET preferred_embedding = CAST(:embedding AS vector) WHERE id = :userId", nativeQuery = true)
    void updatePreferredEmbedding(@Param("userId") Long userId, @Param("embedding") String embedding);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            UPDATE member u
            SET activity_embedding_avg = CAST(sub.avg_vec AS vector)
            FROM (
                SELECT al.user_id,
                       avg(a.description_embedding) AS avg_vec
                FROM activity_log al
                JOIN activity a ON al.activity_id = a.id
                WHERE al.action_type = 'SELECT' AND a.description_embedding IS NOT NULL
                GROUP BY al.user_id
            ) sub
            WHERE u.id = sub.user_id
            """, nativeQuery = true)
    int updateActivityEmbeddingAvgForUsers();
}