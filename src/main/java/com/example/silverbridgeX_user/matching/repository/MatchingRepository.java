package com.example.silverbridgeX_user.matching.repository;

import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<MatchRequest, Long> {
}
