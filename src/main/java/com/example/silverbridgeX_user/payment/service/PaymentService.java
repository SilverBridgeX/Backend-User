package com.example.silverbridgeX_user.payment.service;

import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.global.util.RestTemplateUtil;
import com.example.silverbridgeX_user.payment.converter.PaymentConverter;
import com.example.silverbridgeX_user.payment.domain.Payment;
import com.example.silverbridgeX_user.payment.dto.PaymentDto;
import com.example.silverbridgeX_user.payment.repository.PaymentRepository;
import com.example.silverbridgeX_user.user.domain.User;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RestTemplateUtil restTemplateUtil;

    @Value("${kakaopay.secret_key}")
    private String secretKey;

    @Value("${kakaopay.cid}")
    private String cid;

    public static final String BASE_URL = "https://open-api.kakaopay.com/online/v1/payment";
    public static final String READY_URL = BASE_URL + "/ready";
    public static final String APPROVE_URL = BASE_URL + "/approve";
    public static final String CANCEL_URL = BASE_URL + "/cancel";
    public static final String SUBSCRIBE_URL = BASE_URL + "/subscription";
    public static final String SUBSCRIBE_STATUS_URL = BASE_URL + "/manage/subscription/status";
    public static final String SUBSCRIBE_CANCEL_URL = BASE_URL + "/manage/subscription/inactive";

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "SECRET_KEY " + secretKey;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }

    public PaymentDto.KakaoReadyResponse kakaoPayReady(Long userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", String.valueOf(userId));
        parameters.put("item_name", "은빛동행 구독");
        parameters.put("quantity", "1");
        parameters.put("total_amount", "9900");
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://15.165.17.95/user/payment/success?userId=" + userId);
        parameters.put("fail_url", "http://15.165.17.95/user/payment/fail");
        parameters.put("cancel_url", "http://15.165.17.95/user/payment/cancel");

        return restTemplateUtil.post(READY_URL, parameters, getHeaders(), PaymentDto.KakaoReadyResponse.class);
    }

    public PaymentDto.KakaoApproveResponse approveResponse(String pgToken, Long userId) {
        Payment payment = paymentRepository.getLatestKakaoPayInfo(userId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.TID_NOT_EXIST));
        String tid = payment.getTid();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", "USER_ID");
        parameters.put("pg_token", pgToken);

        return restTemplateUtil.post(APPROVE_URL, parameters, getHeaders(), PaymentDto.KakaoApproveResponse.class);
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

        return restTemplateUtil.post(CANCEL_URL, parameters, getHeaders(), PaymentDto.KakaoCancelResponse.class);
    }

    public PaymentDto.KakaoApproveResponse approveSubscribeResponse(String sid, Long userId) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);
        parameters.put("partner_order_id", "ORDER_ID");
        parameters.put("partner_user_id", String.valueOf(userId));
        parameters.put("item_name", "BodyCheck 구독");
        parameters.put("quantity", "1");
        parameters.put("total_amount", "4900");
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");

        return restTemplateUtil.post(SUBSCRIBE_URL, parameters, getHeaders(), PaymentDto.KakaoApproveResponse.class);
    }

    public PaymentDto.KakaoSubscribeStatusResponse subscribeStatusResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        return restTemplateUtil.post(SUBSCRIBE_STATUS_URL, parameters, getHeaders(),
                PaymentDto.KakaoSubscribeStatusResponse.class);
    }

    public PaymentDto.KakaoSubscribeCancelResponse subscribeCancelResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        return restTemplateUtil.post(SUBSCRIBE_CANCEL_URL, parameters, getHeaders(),
                PaymentDto.KakaoSubscribeCancelResponse.class);
    }

    public Payment getKakaoPayInfo(Long userId) {
        Payment kakaoPay = paymentRepository.getLatestKakaoPayInfo(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        return kakaoPay;
    }

    public boolean existKakaoPayLog(Long userId) {
        return paymentRepository.existsByUserId(userId);
    }

    public void saveTid(Long userId, String tid) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Payment kakaoPay = PaymentConverter.toKakaoPayTid(tid, user);

        paymentRepository.save(kakaoPay);
    }

    public void saveSid(PaymentDto.KakaoApproveResponse kakaoApproveResponse) {

        Payment kakaoPay = paymentRepository.findByTid(kakaoApproveResponse.getTid())
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        kakaoPay.updateSid(kakaoApproveResponse.getSid());

        paymentRepository.save(kakaoPay);
    }

    public void savePayInfo(Long userId, PaymentDto.KakaoApproveResponse kakaoApproveResponse) {
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        Payment kakaoPay = PaymentConverter.toKakaoPay(kakaoApproveResponse.getTid(), kakaoApproveResponse.getSid(),
                member);

        paymentRepository.save(kakaoPay);
    }

    public void cancelPay(Long userId) {
        Payment kakaoPay = paymentRepository.getLatestKakaoPayInfo(userId)
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
                                        kakaoPay.getSid(), kakaoPay.getUser().getId());

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
