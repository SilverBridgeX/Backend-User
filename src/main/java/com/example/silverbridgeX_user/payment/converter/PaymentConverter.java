package com.example.silverbridgeX_user.payment.converter;

import com.example.silverbridgeX_user.payment.domain.Payment;
import com.example.silverbridgeX_user.payment.dto.PaymentDto;
import com.example.silverbridgeX_user.user.domain.User;

public class PaymentConverter {

    public static Payment toKakaoPayTid(String tid, User user) {
        return Payment.builder()
                .user(user)
                .tid(tid)
                .build();

    }

    public static Payment toKakaoPay(String tid, String sid, User user) {
        return Payment.builder()
                .user(user)
                .tid(tid)
                .sid(sid)
                .build();

    }

    public static PaymentDto.KakaoPayStatus toKakaoPayStatus(boolean isLogExist,
                                                             PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse) {
        String status;
        if (!isLogExist) {
            // 결제 시도 이력이 없으면 무조건 INACTIVE
            status = "INACTIVE";
        } else {
            // 결제 시도 이력이 있으면 실제 상태 조회
            status = kakaoSubscribeStatusResponse.getStatus();
        }

        String last_approved_at;
        if (kakaoSubscribeStatusResponse.getLast_approved_at() == null
                || kakaoSubscribeStatusResponse.getLast_approved_at().isEmpty()) {
            last_approved_at = kakaoSubscribeStatusResponse.getCreated_at();
        } else {
            last_approved_at = kakaoSubscribeStatusResponse.getLast_approved_at();
        }

        return PaymentDto.KakaoPayStatus.builder()
                .isLogExist(isLogExist)
                .status(status)
                .last_approved_at(last_approved_at)
                .build();
    }
}
