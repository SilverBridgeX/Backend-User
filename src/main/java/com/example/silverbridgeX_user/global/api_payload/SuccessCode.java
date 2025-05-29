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
    USER_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2011", "회원가입& 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),
    USER_NICKNAME_SUCCESS(HttpStatus.OK, "USER_2004", "닉네임 생성/수정이 완료되었습니다."),
    USER_INFO_UPDATE_SUCCESS(HttpStatus.OK, "USER_2005", "회원 정보 수정이 완료 되었습니다."),
    USER_INFO_VIEW_SUCCESS(HttpStatus.OK, "USER_2006", "회원 정보 조회가 완료 되었습니다."),
    USER_PROFILE_IMAGE_UPDATE_SUCCESS(HttpStatus.OK, "USER_2007", "프로필 사진 이미지 업로드가 완료 되었습니다."),
    USER_MYPAGE_VIEW_SUCCESS(HttpStatus.OK, "USER_2010", "마이페이지 정보 조회가 완료되었습니다."),

    // Recommend Activity
    RECOMMEND_ACTIVITY_VIEW_LIST_SUCCESS(HttpStatus.OK, "ACTIVITY_2001", "추천하는 활동 리스를 반환 완료했습니다."),
    RECOMMEND_ACTIVITY_SELECT_LOG_SUCCESS(HttpStatus.OK, "ACTIVITY_2002", "선택한 활동 로그와 selected 간선 저장 완료했습니다."),
    RECOMMEND_ACTIVITY_VIEW_LOG_SUCCESS(HttpStatus.OK, "ACTIVITY_2002", "열람한 활동 로그를 저장 완료했습니다."),

    // MatchRequest
    MATCH_REQUEST_SUCCESS(HttpStatus.OK, "MATCH_REQUEST_2001", "매치 신청이 완료되었습니다."),
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
