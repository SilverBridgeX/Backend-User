package com.example.silverbridgeX_user.payment.repository;

import com.example.silverbridgeX_user.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUser_Id(Long userId);
    Optional<Payment> findByUser_Id(Long userId);
    Optional<Payment> findByTid(String tid);
    @Query("SELECT k FROM Payment k JOIN FETCH k.user WHERE k.sid IS NOT NULL")
    List<Payment> findAllWithMemberAndSidNotNull();
}
