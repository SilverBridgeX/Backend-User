package com.example.silverbridgeX_user.payment.repository;

import com.example.silverbridgeX_user.payment.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUserId(Long userId);

    Optional<Payment> findByTid(String tid);

    boolean existsByTid(String tid);

    @Query("SELECT k FROM Payment k JOIN FETCH k.user WHERE k.sid IS NOT NULL")
    List<Payment> findAllWithMemberAndSidNotNull();

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    Optional<Payment> getLatestKakaoPayInfo(@Param("userId") Long userId);
}
