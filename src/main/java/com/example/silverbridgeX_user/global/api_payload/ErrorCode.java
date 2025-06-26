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

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_4041", "존재하지 않는 회원입니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "USER_4042", "해당 email을 가진 회원이 없습니다."),
    USER_NOT_FOUND_BY_USERNAME(HttpStatus.NOT_FOUND, "USER_4043", "해당 key를 가진 회원이 없습니다."),
    USER_NOT_OLDER(HttpStatus.FORBIDDEN, "USER_4031", "해당 유저가 노인이 아니랍니다."),
    USER_NOT_GUARDIAN(HttpStatus.FORBIDDEN, "USER_4032", "해당 유저가 보호자가 아닙니다."),
    USER_ROLE_DIFFERENT(HttpStatus.FORBIDDEN, "USER_4033", "해당 유저의 role이 틀립니다."),
    USER_ALREADY_MEMBER(HttpStatus.CONFLICT, "USER_4091", "이미 회원가입된 유저입니다."),

    // Jwt
    WRONG_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "JWT_4041", "일치하는 리프레시 토큰이 없습니다."),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "JWT_4032", "유효하지 않은 토큰입니다."),
    TOKEN_NO_AUTH(HttpStatus.FORBIDDEN, "JWT_4033", "권한 정보가 없는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_4011", "토큰 유효기간이 만료되었습니다."),

    // Activity
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVITY_4041", "존재하지 않는 활동입니다."),

    // Recommend Activity
    RECOMMEND_ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RECOMMEND_4041", "존재하지 않는 추천입니다."),

    // Payment
    TID_NOT_EXIST(HttpStatus.BAD_REQUEST, "PAYMENT_4001", "tid가 존재하지 않습니다."),
    TID_SID_UNSUPPORTED(HttpStatus.BAD_REQUEST, "PAYMENT_4002", "지원되지 않는 tid, sid 입니다."),
    SID_NOT_EXIST(HttpStatus.BAD_REQUEST, "PAYMENT_4003", "sid가 존재하지 않습니다."),
    TID_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "PAYMENT_4004", "tid가 이미 존재합니다."),

    // Match
    MATCH_NOT_EXIST(HttpStatus.BAD_REQUEST, "MATCH_4001", "매치 신청 정보가 존재하지 않습니다."),
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
