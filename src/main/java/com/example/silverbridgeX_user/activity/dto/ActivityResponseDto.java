package com.example.silverbridgeX_user.activity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ActivityResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FestivalItemDto {
        @JsonProperty("fstvlNm")
        private String name; // 축제명

        @JsonProperty("fstvlCo")
        private String description; // 축제 내용

        @JsonProperty("rdnmadr")
        private String streetAddress; // 도로명주소

        @JsonProperty("lnmadr")
        private String lotNumberAddress; // 지번 주소

        @JsonProperty("latitude")
        private String latitude; // 위도

        @JsonProperty("longitude")
        private String longitude; // 경도

        @JsonProperty("homepageUrl")
        private String homepageUrl; // 홈페이지 주소

        @JsonProperty("phoneNumber")
        private String phoneNumber; // 전화번호

        @JsonProperty("relateInfo")
        private String relateInfo; // 관련 정보

        @JsonProperty("fstvlStartDate")
        private String fstvlStartDate; // 축제 시작 일자

        @JsonProperty("fstvlEndDate")
        private String fstvlEndDate; // 축제 종료 일자

        @JsonProperty("mnnstNm")
        private String hostOrg1; // 주관기관명

        @JsonProperty("auspcInsttNm")
        private String hostOrg2; // 후최기관명

        @JsonProperty("suprtInsttNm")
        private String support; // 후원기관명

        @JsonProperty("referenceDate")
        private String referenceDate; // 데이터 기준 일자

        @JsonProperty("insttCode")
        private String insttCode; // 제공기관 코드

        @JsonProperty("insttNm")
        private String insttNm; // 제공기관 기관병

        @JsonProperty("opar")
        private String opar; // 개최장소

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TourSpotItemDto {
        @JsonProperty("trrsrtNm")
        private String name; // 관광지명

        @JsonProperty("trrsrtIntrcn")
        private String description; // 관광지 소개

        @JsonProperty("rdnmadr")
        private String streetAddress; // 도로명 주소

        @JsonProperty("lnmadr")
        private String lotNumberAddress; // 지번 주소

        @JsonProperty("latitude")
        private String latitude; // 위도

        @JsonProperty("longitude")
        private String longitude; // 경도

        @JsonProperty("ar")
        private String area; // 면적

        @JsonProperty("trrsrtSe")
        private String classification; // 관광지 구분

        @JsonProperty("phoneNumber")
        private String phoneNumber; // 관리기관 전화번호

        @JsonProperty("institutionNm")
        private String institutionNm; // 관리기관명

        @JsonProperty("insttNm")
        private String insttNm; // 관리기관명

        @JsonProperty("referenceDate")
        private String referenceDate; // 데이터 기준 일자

        @JsonProperty("insttCode")
        private String insttCode; // 제공기관 코드

        @JsonProperty("cnvnncFclty")
        private String cnvnncFclty; // 공공 편익 시설 정보

        @JsonProperty("recrtClturFclty")
        private String recrtClturFclty; // 휴양 및 문화시설 정보

        @JsonProperty("stayngInfo")
        private String stayngInfo; // 숙박 시설 정보

        @JsonProperty("mvmAmsmtFclty")
        private String mvmAmsmtFclty; // 운동 및 오락시설 정보

        @JsonProperty("hospitalityFclty")
        private String hospitalityFclty; // 접객 시설 정보

        @JsonProperty("sportFclty")
        private String sportFclty; // 지원 시설 정보

        @JsonProperty("appnDate")
        private String appnDate; // 지정 일자

        @JsonProperty("aceptncCo")
        private String aceptncCo; // 수용 인원 수

        @JsonProperty("prkplceCo")
        private String prkplceCo; // 주차 가능 수
    }

}