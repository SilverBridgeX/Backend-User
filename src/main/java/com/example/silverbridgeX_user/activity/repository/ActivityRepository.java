package com.example.silverbridgeX_user.activity.repository;

import com.example.silverbridgeX_user.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByName(String name); // 중복 방지
}
