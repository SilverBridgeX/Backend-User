package com.example.silverbridgeX_user.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    /// Common
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    // User
    USER_SOCIAL_SIGNIN_SUCCESS(HttpStatus.CREATED, "USER_2011", "소셜 회원가입이 완료되었습니다."),
    USER_SOCIAL_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2012", "소셜 로그인이 완료되었습니다."),
    USER_KEY_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2013", "KEY 로그인이 완료되었습니다."),
    USER_KAKAO_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2014", "카카오 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),
    USER_NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "USER_2004", "닉네임 수정이 완료되었습니다."),
    USER_ADDRESS_UPDATE_SUCCESS(HttpStatus.OK, "USER_2005", "주소지 수정이 완료되었습니다."),
    USER_MYPAGE_VIEW_SUCCESS(HttpStatus.OK, "USER_2006", "마이페이지 정보 조회가 완료되었습니다."),
    USER_GUARDIAN_CONNECT_OLDER_SUCCESS(HttpStatus.OK, "USER_2007", "보호자의 노인 연결이 완료되었습니다."),
    USER_GUARDIAN_REGISTER_OLDER_SUCCESS(HttpStatus.OK, "USER_2008", "보호자의 노인 등록이 완료되었습니다."),

    // Recommend Activity
    RECOMMEND_ACTIVITY_VIEW_LIST_SUCCESS(HttpStatus.OK, "ACTIVITY_2001", "추천하는 활동 리스를 반환 완료했습니다."),
    RECOMMEND_ACTIVITY_SELECT_LOG_SUCCESS(HttpStatus.OK, "ACTIVITY_2002", "선택한 활동 로그와 selected 간선 저장 완료했습니다."),
    RECOMMEND_ACTIVITY_VIEW_LOG_SUCCESS(HttpStatus.OK, "ACTIVITY_2002", "열람한 활동 로그를 저장 완료했습니다."),

    // MatchRequest
    MATCH_REQUEST_SUCCESS(HttpStatus.OK, "MATCH_REQUEST_2001", "매치 신청이 완료되었습니다."),

    // Payment
    PAYMENT_URL_CREATE_SUCCESS(HttpStatus.OK, "PAYMENT_2001", "카카오페이 URL을 생성하였습니다."),
    PAYMENT_CANCEL_SUCCESS(HttpStatus.OK, "PAYMENT_2002", "결제 취소가 완료되었습니다."),
    PAYMENT_SUBSCRIBE_SUCCESS(HttpStatus.OK, "PAYMENT_2003", "구독이 완료되었습니다."),
    PAYMENT_SUBSCRIBE_CANCEL_SUCCESS(HttpStatus.OK, "PAYMENT_2004", "구독 취소가 완료되었습니다."),
    PAYMENT_VIEW_SUBSCRIBE_STATUS_SUCCESS(HttpStatus.OK, "PAYMENT_2005", "구독 상태를 반환 완료하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
