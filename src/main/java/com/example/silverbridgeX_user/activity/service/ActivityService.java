package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityType;
import com.example.silverbridgeX_user.activity.dto.ActivityResponseDto.FestivalItemDto;
import com.example.silverbridgeX_user.activity.dto.ActivityResponseDto.TourSpotItemDto;
import com.example.silverbridgeX_user.activity.repository.ActivityNativeRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.api_payload.ErrorCode;
import com.example.silverbridgeX_user.global.exception.GeneralException;
import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final EmbeddingClient embeddingClient;
    private final ActivityNativeRepository activityNativeRepository;
    private final ActivityApiService activityApiService;

    private final org.neo4j.driver.Driver driver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    String festivalApiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api";
    String tourSpotApiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_trrsrt_api";
    String addressToCoordinateApiurl = "https://api.vworld.kr/req/address?service=address&format=json&request=getCoord&refine=false";

    public void fetchAndSaveFestivalData() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        int page = 1;
        while (true) {
            try {
                String urlStr = activityApiService.buildUrl(festivalApiUrl, page);
                String json = activityApiService.getJsonFromUrl(urlStr);
                JsonNode items = activityApiService.parseItems(json);

                if (items == null || !items.isArray() || items.isEmpty()) {
                    log.info("page {} 응답 없음. 중단.", page);
                    return;
                }

                executor.submit(() -> {
                    for (JsonNode item : items) {
                        try {
                            FestivalItemDto dto = objectMapper.treeToValue(item, FestivalItemDto.class);

                            if (LocalDate.parse(dto.getFstvlEndDate()).isBefore(LocalDate.now())) {
                                continue;
                            }

                            if (!activityRepository.existsByName(dto.getName())) {
                                Activity activity = Activity.builder()
                                        .name(dto.getName())
                                        .description(dto.getDescription())
                                        .streetAddress(dto.getStreetAddress())
                                        .lotNumberAddress(dto.getLotNumberAddress())
                                        .latitude(dto.getLatitude())
                                        .longitude(dto.getLongitude())
                                        .startDate(LocalDate.parse(dto.getFstvlStartDate()))
                                        .endDate(LocalDate.parse(dto.getFstvlEndDate()))
                                        .homepageUrl(dto.getHomepageUrl())
                                        .phoneNumber(dto.getPhoneNumber())
                                        .activityType(ActivityType.FESTIVAL)
                                        .chosen(0L)
                                        .shown(0L)
                                        .CTR(0.0)
                                        .build();
                                activityRepository.save(activity);

                                String vectorLiteral = embeddingClient.getEmbeddingLiteral(dto.getDescription());
                                activityNativeRepository.updateVectorByName(dto.getName(), vectorLiteral);

                                log.info("저장됨: " + dto.getName());
                            }

                        } catch (Exception e) {
                            log.error("항목 처리 중 오류 발생", e);
                        }
                    }
                });
                page++;
                Thread.sleep(100); // 속도 제한

            } catch (Exception e) {
                log.error("page {} 처리 중 오류 발생", page, e);
                break;
            }
        }
    }

    public void fetchAndSaveTourSpotData() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        int page = 1;
        while (true) {
            try {
                String urlStr = activityApiService.buildUrl(tourSpotApiUrl, page);
                String json = activityApiService.getJsonFromUrl(urlStr);
                JsonNode items = activityApiService.parseItems(json);

                if (items == null || !items.isArray() || items.isEmpty()) {
                    log.info("page {} 응답 없음. 중단.", page);
                    return;
                }

                executor.submit(() -> {
                    for (JsonNode item : items) {
                        try {
                            TourSpotItemDto dto = objectMapper.treeToValue(item, TourSpotItemDto.class);

                            if (!activityRepository.existsByName(dto.getName())) {
                                Activity activity = Activity.builder()
                                        .name(dto.getName())
                                        .description(dto.getDescription())
                                        .streetAddress(dto.getStreetAddress())
                                        .lotNumberAddress(dto.getLotNumberAddress())
                                        .latitude(dto.getLatitude())
                                        .longitude(dto.getLongitude())
                                        .startDate(LocalDate.parse(dto.getReferenceDate()))
                                        .endDate(LocalDate.parse("2999-12-31"))
                                        .phoneNumber(dto.getPhoneNumber())
                                        .activityType(ActivityType.TOUR_SPOT)
                                        .chosen(0L)
                                        .shown(0L)
                                        .CTR(0.0)
                                        .build();
                                activityRepository.save(activity);

                                String vectorLiteral = embeddingClient.getEmbeddingLiteral(dto.getDescription());
                                activityNativeRepository.updateVectorByName(dto.getName(), vectorLiteral);

                                log.info("저장됨: {}", dto.getName());
                            }
                        } catch (Exception e) {
                            log.error("항목 처리 중 오류 발생", e);
                        }
                    }
                });
                page++;
                Thread.sleep(100); // 속도 제한

            } catch (Exception e) {
                log.error("page {} 처리 중 오류 발생", page, e);
                break;
            }
        }
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
                    urlStr = buildCoordinateUrl("ROAD", activity.getStreetAddress());
                } else if (activity.getLotNumberAddress() != null) {
                    urlStr = buildCoordinateUrl("PARCEL", activity.getLotNumberAddress());
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

    String key = "";

    private String buildCoordinateUrl(String type, String address) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(addressToCoordinateApiurl);
        urlBuilder.append("&key=" + key);
        urlBuilder.append("&type=" + type); // PARCEL : 지번주소, ROAD : 도로명주소
        urlBuilder.append("&address=" + URLEncoder.encode("경상북도 영주시 봉현면 오현리 763-1", StandardCharsets.UTF_8));
        return urlBuilder.toString();
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
    public void test() throws Exception {
        Activity activity = activityRepository.findById(Long.valueOf(102))
                .orElseThrow(() -> GeneralException.of(ErrorCode.ACTIVITY_NOT_FOUND));
        String urlStr = buildCoordinateUrl("PARCEL", activity.getLotNumberAddress());
        String json = activityApiService.getJsonFromUrl(urlStr);
        System.out.println(json);

    }

    public void insertActivitiesNeo4j() {
        List<Activity> activities = activityRepository.findAll();

        for (Activity activity : activities) {
            try (Session session = driver.session()) {
                session.writeTransaction(tx -> {
                    tx.run("""
                                    MERGE (a:Activity {id: $id})
                                    SET a.name = $name,
                                        a.description = $description,
                                        a.embedding = $embedding,
                                        a.latitude = $latitude,
                                        a.longitude = $longitude,
                                        a.startDate = $startDate,
                                        a.endDate = $endDate
                                    """,
                            Values.parameters(
                                    "id", activity.getId(),
                                    "name", activity.getName(),
                                    "description", activity.getDescription(),
                                    "embedding", activity.getDescriptionEmbedding(),
                                    "latitude", activity.getLatitude(),
                                    "longitude", activity.getLongitude(),
                                    "startDate", activity.getStartDate(),
                                    "endDate", activity.getEndDate()
                            ));
                    return null;
                });
            }
        }
    }

}
