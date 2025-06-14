package com.example.silverbridgeX_user.activity.repository;

import com.example.silverbridgeX_user.activity.domain.Activity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByName(String name); // 중복 방지

    @Query(value = """
                SELECT id FROM activity
                WHERE id NOT IN (:excluded)
                ORDER BY random()
                LIMIT :limit
            """, nativeQuery = true)
    List<Long> pickRandom(int limit, Collection<Long> excluded);

}
