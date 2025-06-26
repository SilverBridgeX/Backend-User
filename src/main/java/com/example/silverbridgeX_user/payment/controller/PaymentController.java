package com.example.silverbridgeX_user.payment.controller;

import com.example.silverbridgeX_user.global.api_payload.ApiResponse;
import com.example.silverbridgeX_user.global.api_payload.SuccessCode;
import com.example.silverbridgeX_user.payment.domain.Payment;
import com.example.silverbridgeX_user.payment.dto.PaymentDto;
import com.example.silverbridgeX_user.payment.service.PaymentService;
import com.example.silverbridgeX_user.user.jwt.CustomUserDetails;
import com.example.silverbridgeX_user.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/ready")
    @Operation(summary = "카카오페이 URL 생성 API", description = "카카오페이 URL을 생성하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoReadyResponse> readyToKakaoPay(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = userService.findByUserName(customUserDetails.getUsername()).getId();

        PaymentDto.KakaoReadyResponse kakaoReadyResponse = paymentService.kakaoPayReady(userId);

        paymentService.saveTid(userId, kakaoReadyResponse.getTid());

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_URL_CREATE_SUCCESS, kakaoReadyResponse);
    }

    @PostMapping("/ready/key")
    @Operation(summary = "카카오페이 URL 생성 API", description = "key를 이용하여 카카오페이 URL을 생성하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoReadyResponse> readyToKakaoPay(
            @RequestParam("id") String key
    ) {
        Long userId = userService.findByUserName(key).getId();

        PaymentDto.KakaoReadyResponse kakaoReadyResponse = paymentService.kakaoPayReady(userId);

        paymentService.saveTid(userId, kakaoReadyResponse.getTid());

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_URL_CREATE_SUCCESS, kakaoReadyResponse);
    }

    @GetMapping("/success")
    public ModelAndView afterPayRequest(
            @RequestParam("pg_token") String pgToken, @RequestParam("userId") Long userId
    ) {
        PaymentDto.KakaoApproveResponse kakaoApproveResponse = paymentService.approveResponse(pgToken, userId);

        ModelAndView modelAndView = new ModelAndView("success"); // "success"는 템플릿 파일 이름
        modelAndView.addObject("paymentInfo", kakaoApproveResponse);

        paymentService.saveSid(kakaoApproveResponse);

        return modelAndView;
    }

    @GetMapping("/fail")
    public String fail() {
        return "fail";
    }

    @GetMapping("/cancel")
    public ApiResponse<PaymentDto.KakaoCancelResponse> refund(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Long userId = userService.findByUserName(customUserDetails.getUsername()).getId();

        Payment kakaoPay = paymentService.getKakaoPayInfo(userId);

        PaymentDto.KakaoCancelResponse kakaoCancelResponse = paymentService.cancelResponse(kakaoPay.getTid());

        paymentService.cancelPay(userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_CANCEL_SUCCESS, kakaoCancelResponse);
    }

    @GetMapping("/cancel/key")
    public ApiResponse<PaymentDto.KakaoCancelResponse> refund(
            @RequestParam("id") String key
    ) {

        Long userId = userService.findByUserName(key).getId();

        Payment kakaoPay = paymentService.getKakaoPayInfo(userId);

        PaymentDto.KakaoCancelResponse kakaoCancelResponse = paymentService.cancelResponse(kakaoPay.getTid());

        paymentService.cancelPay(userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_CANCEL_SUCCESS, kakaoCancelResponse);
    }

    @PostMapping("/subscribe")
    public ApiResponse<PaymentDto.KakaoApproveResponse> subscribePayRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = userService.findByUserName(customUserDetails.getUsername()).getId();

        Payment kakaoPay = paymentService.getKakaoPayInfo(userId);

        PaymentDto.KakaoApproveResponse kakaoApproveResponse = paymentService.approveSubscribeResponse(
                kakaoPay.getSid(), userId);

        paymentService.savePayInfo(userId, kakaoApproveResponse);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_SUBSCRIBE_SUCCESS, kakaoApproveResponse);
    }

    @PostMapping("/subscribe/key")
    public ApiResponse<PaymentDto.KakaoApproveResponse> subscribePayRequest(
            @RequestParam("id") String key
    ) {
        Long userId = userService.findByUserName(key).getId();

        Payment kakaoPay = paymentService.getKakaoPayInfo(userId);

        PaymentDto.KakaoApproveResponse kakaoApproveResponse = paymentService.approveSubscribeResponse(
                kakaoPay.getSid(), userId);

        paymentService.savePayInfo(userId, kakaoApproveResponse);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_SUBSCRIBE_SUCCESS, kakaoApproveResponse);
    }

    @PostMapping("/subscribe/cancel")
    @Operation(summary = "카카오페이 구독 취소 API", description = "카카오페이 구독을 취소하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoSubscribeCancelResponse> subscribeCancelRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = userService.findByUserName(customUserDetails.getUsername()).getId();

        PaymentDto.KakaoSubscribeCancelResponse kakaoSubscribeCancelResponse = paymentService.subscribeCancelResponse(
                userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_URL_CREATE_SUCCESS, kakaoSubscribeCancelResponse);
    }

    @PostMapping("/subscribe/cancel/key")
    @Operation(summary = "카카오페이 구독 취소 API", description = "key를 이용하여 카카오페이 구독을 취소하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoSubscribeCancelResponse> subscribeCancelRequest(
            @RequestParam("id") String key
    ) {
        Long userId = userService.findByUserName(key).getId();

        PaymentDto.KakaoSubscribeCancelResponse kakaoSubscribeCancelResponse = paymentService.subscribeCancelResponse(
                userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_URL_CREATE_SUCCESS, kakaoSubscribeCancelResponse);
    }

    @GetMapping("/subscribe/status")
    @Operation(summary = "카카오페이 구독 상태 확인 API", description = "카카오페이 구독 상태를 확인하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoPayStatus> subscribeStatusRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = userService.findByUserName(customUserDetails.getUsername()).getId();

        PaymentDto.KakaoPayStatus kakaoPayStatus = paymentService.getSubscribeStatus(userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_VIEW_SUBSCRIBE_STATUS_SUCCESS, kakaoPayStatus);
    }

    @GetMapping("/subscribe/status/key")
    @Operation(summary = "카카오페이 구독 상태 확인 API", description = "key를 이용하여 카카오페이 구독 상태를 확인하는 API 입니다.")
    public ApiResponse<PaymentDto.KakaoPayStatus> subscribeStatusRequest(
            @RequestParam("id") String key
    ) {
        Long userId = userService.findByUserName(key).getId();

        PaymentDto.KakaoPayStatus kakaoPayStatus = paymentService.getSubscribeStatus(userId);

        return ApiResponse.onSuccess(SuccessCode.PAYMENT_VIEW_SUBSCRIBE_STATUS_SUCCESS, kakaoPayStatus);
    }
}
