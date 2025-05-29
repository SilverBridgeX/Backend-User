package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressToCoordinate {
    private final ActivityRepository activityRepository;
    private final ActivityApiService activityApiService;
    String key = "";
    String url = "https://api.vworld.kr/req/address?service=address&format=json&request=getCoord&refine=false";

    public String buildUrl(String type, String address) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("&key=" + key);
        urlBuilder.append("&type=" + type); // PARCEL : 지번주소, ROAD : 도로명주소
        urlBuilder.append("&address=" + URLEncoder.encode("경상북도 영주시 봉현면 오현리 763-1", StandardCharsets.UTF_8));
        return urlBuilder.toString();
    }

    public void fetchAndSaveCoordinate() throws Exception {
        List<Activity> activities = activityRepository.findAll();
        System.out.println(activities.size());

        for (Activity activity : activities) {

            try {
                if (activity.getLatitude().startsWith("\"")) {
                    change(activity);
                }

                if (!activity.getLongitude().isEmpty() || !activity.getLatitude().isEmpty()) {
                    continue;
                }

                String urlStr = null;

                if (activity.getStreetAddress() != null) {
                    urlStr = buildUrl("ROAD", activity.getStreetAddress());
                } else if (activity.getLotNumberAddress() != null) {
                    urlStr = buildUrl("PARCEL", activity.getLotNumberAddress());
                }

                if (urlStr == null) {
                    continue;
                }

                String json = activityApiService.getJsonFromUrl(urlStr);
                System.out.println(json);
                JsonNode point = activityApiService.parsePoint(json);

                if (!point.isMissingNode()) {
                    String x = String.valueOf(point.get("x"));
                    String y = String.valueOf(point.get("y"));

                    activity.updateCoordinate(x, y);
                    activityRepository.save(activity);
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void change(Activity activity) {
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
//    public void test() throws Exception {
//        Activity activity = activityRepository.findById(Long.valueOf(102))
//                .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
//        String urlStr = buildUrl("PARCEL", activity.getLotNumberAddress());
//        String json = activityApiService.getJsonFromUrl(urlStr);
//        System.out.println(json);
//
//    }

}
