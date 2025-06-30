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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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

        try {
            return restTemplateUtil.post(READY_URL, parameters, getHeaders(), PaymentDto.KakaoReadyResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Transactional
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

        try {
            return restTemplateUtil.post(APPROVE_URL, parameters, getHeaders(), PaymentDto.KakaoApproveResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.PAYMENT_FAILED);
        }
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

        try {
            return restTemplateUtil.post(CANCEL_URL, parameters, getHeaders(), PaymentDto.KakaoCancelResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.PAYMENT_FAILED);
        }
    }

    @Transactional
    public PaymentDto.KakaoApproveResponse approveSubscribeResponse(String sid, Long userId) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);
        parameters.put("partner_order_id", String.valueOf(System.currentTimeMillis()));
        parameters.put("partner_user_id", String.valueOf(userId));
        parameters.put("item_name", "BodyCheck 구독");
        parameters.put("quantity", "1");
        parameters.put("total_amount", "4900");
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");

        PaymentDto.KakaoApproveResponse response;
        try {
            response = restTemplateUtil.post(SUBSCRIBE_URL, parameters, getHeaders(),
                    PaymentDto.KakaoApproveResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.PAYMENT_FAILED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        user.enableSubscription();

        return response;
    }

    @Transactional
    public PaymentDto.KakaoSubscribeCancelResponse subscribeCancelResponse(Long userId) {
        Payment kakaoPay = paymentRepository.getLatestKakaoPayInfo(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        String sid = kakaoPay.getSid();
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        PaymentDto.KakaoSubscribeCancelResponse response;
        try {
            response = restTemplateUtil.post(SUBSCRIBE_CANCEL_URL, parameters,
                    getHeaders(),
                    PaymentDto.KakaoSubscribeCancelResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.EXTERNAL_API_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        user.disableSubscription();

        return response;
    }

    public PaymentDto.KakaoSubscribeStatusResponse subscribeStatusResponse(String sid) {
        if (sid == null || sid.isEmpty()) {
            throw new GeneralException(ErrorCode.SID_NOT_EXIST);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("sid", sid);

        try {
            return restTemplateUtil.post(SUBSCRIBE_STATUS_URL, parameters, getHeaders(),
                    PaymentDto.KakaoSubscribeStatusResponse.class);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Payment getKakaoPayInfo(Long userId) {
        Payment kakaoPay = paymentRepository.getLatestKakaoPayInfo(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        return kakaoPay;
    }

    @Transactional
    public void saveTid(Long userId, String tid) {
        if (paymentRepository.existsByTid(tid)) {
            throw new GeneralException(ErrorCode.TID_ALREADY_EXIST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Payment kakaoPay = PaymentConverter.toKakaoPayTid(tid, user);

        paymentRepository.save(kakaoPay);
    }

    @Transactional
    public void saveSid(PaymentDto.KakaoApproveResponse kakaoApproveResponse) {

        Payment kakaoPay = paymentRepository.findTopByTidOrderByIdDesc(kakaoApproveResponse.getTid())
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        kakaoPay.updateSid(kakaoApproveResponse.getSid()); // jpa에서 영속 상태의 엔티티는 setter 호출만해도 dirty checking 반영됨
    }

    @Transactional
    public void savePayInfo(Long userId, PaymentDto.KakaoApproveResponse kakaoApproveResponse) {
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        Payment kakaoPay = PaymentConverter.toKakaoPay(kakaoApproveResponse.getTid(), kakaoApproveResponse.getSid(),
                member);

        paymentRepository.save(kakaoPay);
    }

    @Transactional
    public void cancelPay(Long userId) {
        Payment kakaoPay = paymentRepository.getLatestKakaoPayInfo(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TID_NOT_EXIST));

        paymentRepository.delete(kakaoPay);
    }

    @Transactional
    public PaymentDto.KakaoPayStatus getSubscribeStatus(Long userId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 2. 유저가 활성화 상태가 아니면 → 구독 X 상태로 리턴
        if (!user.isSubscribeActive()) {
            return PaymentConverter.toKakaoPayStatus(false, new PaymentDto.KakaoSubscribeStatusResponse());
        }

        // 3. 최신 결제 정보 조회
        Optional<Payment> optionalKakaoPay = paymentRepository.getLatestKakaoPayInfo(userId);

        // 4. 결제 정보가 없거나, sid가 없으면 → 구독 비활성화 처리 후 구독 X 상태 리턴
        if (optionalKakaoPay.isEmpty() || optionalKakaoPay.get().getSid() == null || optionalKakaoPay.get().getSid()
                .isEmpty()) {
            // 추후 결제 취소가 안되었는데 이전 sid가 없다 -> 이전 정기결제 실패 -> 재시도 로직 수가 에정
            user.disableSubscription();
            return PaymentConverter.toKakaoPayStatus(false, new PaymentDto.KakaoSubscribeStatusResponse());
        }

        // 5. 결제 정보가 있고 sid도 있으면 → 구독 O 상태 리턴
        Payment kakaoPay = optionalKakaoPay.get();
        PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = subscribeStatusResponse(
                kakaoPay.getSid());

        return PaymentConverter.toKakaoPayStatus(true, kakaoSubscribeStatusResponse);
    }


    @Transactional
    public void regularPayment() {
        List<Payment> kakaoPayList = paymentRepository.findAllWithMemberAndSidNotNull();

        kakaoPayList.stream()
                .forEach(kakaoPay -> {
                    if (kakaoPay.getSid() != null && !kakaoPay.getSid().isEmpty()) {
                        PaymentDto.KakaoSubscribeStatusResponse kakaoSubscribeStatusResponse = subscribeStatusResponse(
                                kakaoPay.getSid());

                        // 사용자가 카카오 결제 내역 화면에서 직접 해지 요청을 한 경우, db에 대항 상황이 반영x
                        // -> 결제 상태를 우리 DB에도 반영
                        if (kakaoSubscribeStatusResponse.getStatus().equals("INACTIVE")) {
                            kakaoPay.getUser().disableSubscription();
                            return; // 결제 해지된 유저는 패스
                        }

                        if (kakaoSubscribeStatusResponse.getStatus().equals("ACTIVE")) {
                            String lastApprovedAtStr = kakaoSubscribeStatusResponse.getLast_approved_at();
                            if (lastApprovedAtStr == null || lastApprovedAtStr.isEmpty()) {
                                lastApprovedAtStr = kakaoSubscribeStatusResponse.getCreated_at();
                            }

                            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                            LocalDate lastApprovedAt = LocalDate.parse(lastApprovedAtStr, formatter);

                            LocalDate today = LocalDate.now();

                            // 결제일 + 새로운 달이 되었을 때 결제 요청
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


}
