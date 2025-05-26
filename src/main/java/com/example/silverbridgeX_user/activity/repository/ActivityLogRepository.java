package com.example.silverbridgeX_user.activity.repository;

import com.example.silverbridgeX_user.activity.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
