package com.example.silverbridgeX_user.matching.repository;

import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.matching.domain.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingRepository extends JpaRepository<MatchRequest, Long> {

    @Query("SELECT m FROM MatchRequest m JOIN FETCH m.user WHERE m.status = :status ORDER BY m.createdAt ASC")
    List<MatchRequest> findAllWithUserByStatus(@Param("status") MatchStatus status);
}
