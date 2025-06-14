package com.example.silverbridgeX_user.global.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.converter.CoordinateConverter;
import com.example.silverbridgeX_user.global.dto.CoordinateDto;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoordinateService {
    private final ActivityRepository activityRepository;
    private final ApiService apiService;

    public String addressToCoordinateApiurl = "https://api.vworld.kr/req/address?service=address&format=json&request=getCoord&refine=false";

    @Value("${geocoder.api.key}")
    private String key;

    public CoordinateDto.simpleCoordinateDto getCoordinateByAddress(String type, String address) throws Exception {
        String urlStr = buildCoordinateUrl(type, address);
        assert apiService != null;
        String json = apiService.getJsonFromUrl(urlStr);
        JsonNode point = apiService.parsePoint(json);

        String x = String.valueOf(point.get("x"));
        String y = String.valueOf(point.get("y"));
        x = x.replace("\"", "");
        y = y.replace("\"", "");

        return CoordinateConverter.simpleCoordinateDto(x, y);
    }

    public String buildCoordinateUrl(String type, String address) {
        StringBuilder urlBuilder = new StringBuilder(addressToCoordinateApiurl);
        urlBuilder.append("&key=" + key);
        urlBuilder.append("&type=" + type); // PARCEL : 지번주소, ROAD : 도로명주소
        urlBuilder.append("&address=" + URLEncoder.encode(address, StandardCharsets.UTF_8));
        return urlBuilder.toString();
    }

    public void change(Activity activity) {
        String latitude = activity.getLatitude();
        String longitude = activity.getLongitude();

        boolean updated = false;

        if (latitude != null && latitude.contains("\"")) {
            latitude = latitude.replace("\"", ""); // 모든 " 제거
            activity.updateLatitude(latitude);
            updated = true;
        }

        if (longitude != null && longitude.contains("\"")) {
            longitude = longitude.replace("\"", "");
            activity.updateLongitude(longitude);
            updated = true;
        }

        if (updated) {
            activityRepository.save(activity);
            System.out.println("좌표 문자열 정제 완료: " + activity.getId());
        }
    }

    // 해당 API에서 제공하지 않는 주소들에 대한 2차 검증 로직
    public void test() throws Exception {
        Activity activity = activityRepository.findById(Long.valueOf(102))
                .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
        String urlStr = buildCoordinateUrl("PARCEL", activity.getLotNumberAddress());
        String json = apiService.getJsonFromUrl(urlStr);
        System.out.println(json);

    }
}
