package com.example.silverbridgeX_user.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 서버 개발자에게 문의하세요."),
    INVALID_PARAMETER(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5002", "잘못된 DAY Parameter 가 들어왔습니다. "),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_4041", "존재하지 않는 회원입니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "USER_4042", "존재하지 않는 회원입니다.-EMAIL"),
    USER_NOT_FOUND_BY_USERNAME(HttpStatus.NOT_FOUND, "USER_4043", "존재하지 않는 회원입니다.-USERNAME"),
    ALREADY_USED_NICKNAME(HttpStatus.FORBIDDEN, "USER_4031", "이미 사용중인 닉네임입니다."),
    USER_ADDRESS_NULL(HttpStatus.BAD_REQUEST, "USER_4001", "주소값이 비었거나 NULL입니다."),
    INVALID_PHONE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "USER_4002", "전화번호 형식은 연속된 숫자 11개여야 합니다."),
    SMS_CODE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "USER_4003", "전화번호 인증 코드가 틀렸습니다"),

    // Jwt
    WRONG_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "JWT_4041", "일치하는 리프레시 토큰이 없습니다."),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "JWT_4032", "유효하지 않은 토큰입니다."),
    TOKEN_NO_AUTH(HttpStatus.FORBIDDEN, "JWT_4033", "권한 정보가 없는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_4011", "토큰 유효기간이 만료되었습니다."),

    // Activity
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVITY_4041", "존재하지 않는 활동입니다."),

    // Recommend Activity
    RECOMMEND_ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RECOMMEND_4041", "존재하지 않는 추천입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
