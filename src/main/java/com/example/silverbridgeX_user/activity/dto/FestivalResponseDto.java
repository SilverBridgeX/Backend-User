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
public class FestivalResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FestivalItemDto {
        @JsonProperty("fstvlNm")
        private String name;

        @JsonProperty("fstvlCo")
        private String description;

        @JsonProperty("rdnmadr")
        private String streetAddress;

        @JsonProperty("latitude")
        private String latitude;

        @JsonProperty("longitude")
        private String longitude;

        @JsonProperty("homepageUrl")
        private String homepageUrl;

        @JsonProperty("phoneNumber")
        private String phoneNumber;

        @JsonProperty("fstvlStartDate")
        private String fstvlStartDate;

        @JsonProperty("fstvlEndDate")
        private String fstvlEndDate;

        @JsonProperty("mnnstNm")
        private String hostOrg1;

        @JsonProperty("auspcInsttNm")
        private String hostOrg2;

        @JsonProperty("suprtInsttNm")
        private String support;

        @JsonProperty("relateInfo")
        private String relateInfo;

        @JsonProperty("lnmadr")
        private String lotNumberAddress;

        @JsonProperty("referenceDate")
        private String referenceDate;

        @JsonProperty("insttCode")
        private String insttCode;

        @JsonProperty("insttNm")
        private String insttNm;

        @JsonProperty("opar")
        private String opar;

    }
}
