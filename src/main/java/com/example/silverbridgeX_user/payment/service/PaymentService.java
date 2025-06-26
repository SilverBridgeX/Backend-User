package com.example.silverbridgeX_user.payment.service;

import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.payment.converter.PaymentConverter;
import com.example.silverbridgeX_user.payment.domain.Payment;
import com.example.silverbridgeX_user.payment.dto.PaymentDto;
import com.example.silverbridgeX_user.payment.repository.PaymentRepository;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
@EnableScheduling
public class PaymentService {

    private RestTemplate restTemplate = new RestTemplate();
    private PaymentDto.KakaoReadyResponse kakaoReadyResponse;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${kakaopay.secret_key}")
    private String secretKey;

    @Value("${kakaopay.cid}")
    private String cid;

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "SECRET_KEY " + secretKey;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }

    public PaymentDto.KakaoReadyResponse kakaoPayReady() {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("cid", cid);
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", "USER_ID");
        parameters.put("item_name", "은빛동행 구독");
        parameters.put("quantity", "1");
        parameters.put("total_amount", "9900");
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url",
                "http://15.165.17.95/user/payment/success"); // http://15.165.17.95/user/payment/success http://localhost:8080/payment/success
        parameters.put("fail_url",
                "http://15.165.17.95/user/payment/fail"); // http://15.165.17.95/user/payment/fail http://localhost:8080/payment/fail
        parameters.put("cancel_url",
                "http://15.165.17.95/user/payment/cancel"); // http://15.165.17.95/user/payment/cancel http://localhost:8080/payment/cancel

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        kakaoReadyResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                PaymentDto.KakaoReadyResponse.class);
        return kakaoReadyResponse;
    }

    public PaymentDto.KakaoApproveResponse approveResponse(String pgToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", kakaoReadyResponse.getTid());
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", "USER_ID");
        parameters.put("pg_token", pgToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        PaymentDto.KakaoApproveResponse kakaoApproveResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                requestEntity,
                PaymentDto.KakaoApproveResponse.class);
        return kakaoApproveResponse;
    }

    public PaymentDto.KakaoCancelResponse cancelResponse(String tid) {
        if (tid == null || tid.isEmpty()) {
            throw new GeneralException(ErrorCode.TID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("cancel_amount", "9900");
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "0");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        PaymentDto.KakaoCancelResponse kakaoCancelResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                requestEntity,
                PaymentDto.KakaoCancelResponse.class);
        return kakaoCancelResponse;
    }

    public Payment getKakaoPayInfo(Long userId) {
        Payment kakaoPay = paymentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        return kakaoPay;
    }

    public boolean existKakaoPayLog(Long userId) {
        return paymentRepository.existsByUser_Id(userId);
    }

    public boolean getPremiumState(Long userId) {
        boolean isPremium = false;

        if (paymentRepository.existsByUser_Id(userId)) {
            Payment kakaoPay = paymentRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

            if (kakaoPay.getSid() == null || kakaoPay.getSid().isEmpty()) {
            } else {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("cid", cid);
                parameters.put("sid", kakaoPay.getSid());

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

                PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = restTemplate.postForObject(
                        "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/status",
                        requestEntity,
                        PaymentDto.KakaoSubscribeStatusResponse.class);

                if (kakaoSubscribeStatusResponse.getStatus().equals("ACTIVE")) {
                    isPremium = true;
                } else {
                    String last_approved_at;
                    if (kakaoSubscribeStatusResponse.getLast_approved_at() == null
                            || kakaoSubscribeStatusResponse.getLast_approved_at().isEmpty()) {
                        last_approved_at = kakaoSubscribeStatusResponse.getCreated_at();
                    } else {
                        last_approved_at = kakaoSubscribeStatusResponse.getLast_approved_at();
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime lastApprovedAt = LocalDateTime.parse(last_approved_at, formatter);

                    LocalDateTime oneMonthLater = lastApprovedAt.plusMonths(1).withHour(14).withMinute(0).withSecond(0);

                    LocalDateTime now = LocalDateTime.now();

                    if (now.isBefore(oneMonthLater)) {
                        isPremium = true;
                    }
                }
            }

        }

        return isPremium;
    }

    public PaymentDto.KakaoApproveResponse approveSubscribeResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", "USER_ID");
        parameters.put("item_name", "BodyCheck 구독");
        parameters.put("quantity", "1");
        parameters.put("total_amount", "4900");
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        PaymentDto.KakaoApproveResponse kakaoApproveResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/subscription",
                requestEntity,
                PaymentDto.KakaoApproveResponse.class);
        return kakaoApproveResponse;
    }

    public PaymentDto.KakaoSubscribeCancelResponse subscribeCancelResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        PaymentDto.KakaoSubscribeCancelResponse kakaoSubscribeCancelResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive",
                requestEntity,
                PaymentDto.KakaoSubscribeCancelResponse.class);
        return kakaoSubscribeCancelResponse;
    }

    public PaymentDto.KakaoSubscribeStatusResponse subscribeStatusResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/status",
                requestEntity,
                PaymentDto.KakaoSubscribeStatusResponse.class);
        return kakaoSubscribeStatusResponse;
    }

    public void saveTid(Long userId, String tid) {
        Payment kakaoPay;
        if (paymentRepository.existsByUser_Id(userId)) {
            kakaoPay = paymentRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.TID_SID_UNSUPPORTED));
            kakaoPay.updateTid(tid);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
            kakaoPay = PaymentConverter.toKakaoPayTid(tid, user);
        }
        paymentRepository.save(kakaoPay);
    }

    public void saveSid(PaymentDto.KakaoApproveResponse kakaoApproveResponse) {

        Payment kakaoPay = paymentRepository.findByTid(kakaoApproveResponse.getTid())
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        kakaoPay.updateSid(kakaoApproveResponse.getSid());

        paymentRepository.save(kakaoPay);
    }

    public void savePayInfo(Long userId, PaymentDto.KakaoApproveResponse kakaoApproveResponse) {
        Payment kakaoPay;
        if (paymentRepository.existsByUser_Id(userId)) {
            kakaoPay = paymentRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.TID_SID_UNSUPPORTED));
            kakaoPay.updatePayInfo(kakaoApproveResponse.getTid(), kakaoApproveResponse.getSid());
        } else {
            User member = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
            kakaoPay = PaymentConverter.toKakaoPay(kakaoApproveResponse.getTid(), kakaoApproveResponse.getSid(),
                    member);
        }
        paymentRepository.save(kakaoPay);
    }

    public void cancelPay(Long userId) {
        Payment kakaoPay = paymentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        paymentRepository.delete(kakaoPay);
    }

    public void regularPayment() {
        List<Payment> kakaoPayList = paymentRepository.findAllWithMemberAndSidNotNull();

        kakaoPayList.stream()
                .forEach(kakaoPay -> {
                    if (kakaoPay.getSid() != null && !kakaoPay.getSid().isEmpty()) {
                        PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = subscribeStatusResponse(
                                kakaoPay.getSid());

                        // "ACTIVE" 상태인지 확인
                        if (kakaoSubscribeStatusResponse.getStatus().equals("ACTIVE")) {
                            String lastApprovedAtStr = kakaoSubscribeStatusResponse.getLast_approved_at();
                            if (lastApprovedAtStr == null || lastApprovedAtStr.isEmpty()) {
                                lastApprovedAtStr = kakaoSubscribeStatusResponse.getCreated_at();
                            }

                            // last_approved_at을 LocalDate로 변환
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                            LocalDate lastApprovedAt = LocalDate.parse(lastApprovedAtStr, formatter);

                            LocalDate today = LocalDate.now();

                            // 결제일과 오늘의 일(day)이 같고, 마지막 결제일이 이번 달이 아닌 경우에만 결제 수행
                            if (today.getDayOfMonth() == lastApprovedAt.getDayOfMonth() &&
                                    (today.getYear() != lastApprovedAt.getYear()
                                            || today.getMonthValue() != lastApprovedAt.getMonthValue())) {

                                PaymentDto.KakaoApproveResponse approveResponse = approveSubscribeResponse(
                                        kakaoPay.getSid());

                                savePayInfo(kakaoPay.getUser().getId(), approveResponse);
                            }
                        }
                    }
                });
    }

    public PaymentDto.KakaoPayStatus getSubscribeStatus(Long userId) {
        boolean isLogExist = false;
        PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = new PaymentDto.KakaoSubscribeStatusResponse();

        if (existKakaoPayLog(userId)) {
            Payment kakaoPay = getKakaoPayInfo(userId);

            if (kakaoPay.getSid() != null && !kakaoPay.getSid().isEmpty()) {
                isLogExist = true;
                kakaoSubscribeStatusResponse = subscribeStatusResponse(kakaoPay.getSid());
            }
        }

        return PaymentConverter.toKakaoPayStatus(isLogExist, kakaoSubscribeStatusResponse);
    }

}
