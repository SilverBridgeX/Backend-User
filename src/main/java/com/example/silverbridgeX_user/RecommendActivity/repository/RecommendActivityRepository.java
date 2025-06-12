package com.example.silverbridgeX_user.RecommendActivity.repository;

import com.example.silverbridgeX_user.RecommendActivity.domain.RecommendActivity;
import com.example.silverbridgeX_user.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendActivityRepository extends JpaRepository<RecommendActivity, Long> {
    List<RecommendActivity> findAllByUser(User user);

    void deleteByUser(User user);
}
