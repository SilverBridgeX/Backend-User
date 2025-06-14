package com.example.silverbridgeX_user.payment.scheduler;

import com.example.silverbridgeX_user.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 14 * * ?")
    public void regularPayment() {
        paymentService.regularPayment();
    }
}
