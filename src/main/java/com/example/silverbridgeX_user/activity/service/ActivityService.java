package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityType;
import com.example.silverbridgeX_user.activity.dto.ActivityResponseDto.FestivalItemDto;
import com.example.silverbridgeX_user.activity.dto.ActivityResponseDto.TourSpotItemDto;
import com.example.silverbridgeX_user.activity.repository.ActivityNativeRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.service.ApiService;
import com.example.silverbridgeX_user.global.service.CoordinateService;
import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.example.silverbridgeX_user.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
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
    private final CoordinateService coordinateService;
    private final ApiService apiService;
    private final UserRepository userRepository;

    private final Driver driver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    String festivalApiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api";
    String tourSpotApiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_trrsrt_api";

    public void fetchAndSaveFestivalData() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        int page = 1;
        while (true) {
            try {
                String urlStr = apiService.buildUrl(festivalApiUrl, page);
                String json = apiService.getJsonFromUrl(urlStr);
                JsonNode items = apiService.parseItems(json);

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
                                        .clickNum(0L)
                                        .impressionNum(0L)
                                        .ctr(0.0)
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

    public void fetchAndSaveCoordinate() throws Exception {
        List<Activity> activities = activityRepository.findAll();
        System.out.println(activities.size());

        for (Activity activity : activities) {

            try {
                if (activity.getLatitude().startsWith("\"")) {
                    coordinateService.change(activity);
                }

                if (!activity.getLongitude().isEmpty() || !activity.getLatitude().isEmpty()) {
                    continue;
                }

                String urlStr = null;

                if (activity.getStreetAddress() != null) {
                    urlStr = coordinateService.buildCoordinateUrl("ROAD", activity.getStreetAddress());
                } else if (activity.getLotNumberAddress() != null) {
                    urlStr = coordinateService.buildCoordinateUrl("PARCEL", activity.getLotNumberAddress());
                }

                if (urlStr == null) {
                    continue;
                }

                String json = apiService.getJsonFromUrl(urlStr);
                log.info(json);
                JsonNode point = apiService.parsePoint(json);

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

    public void fetchAndSaveTourSpotData() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        int page = 1;
        while (true) {
            try {
                String urlStr = apiService.buildUrl(tourSpotApiUrl, page);
                String json = apiService.getJsonFromUrl(urlStr);
                JsonNode items = apiService.parseItems(json);

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
                                        .clickNum(0L)
                                        .impressionNum(0L)
                                        .ctr(0.0)
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

    public void insertActivitiesNeo4j() {
        List<Activity> activities = activityRepository.findAll();

        for (Activity activity : activities) {
            List<Float> embeddingVector = parseEmbeddingVector(activity.getDescriptionEmbedding());
            String description = activity.getDescription().length() > 100
                    ? activity.getDescription().substring(0, 100)
                    : activity.getDescription();

            try (Session session = driver.session()) {
                session.writeTransaction(tx -> {
                    tx.run("""
                                    MERGE (a:Activity {id: $id})
                                    SET a.name = $name,
                                        a.description = $description,
                                        a.latitude = $latitude,
                                        a.longitude = $longitude,
                                        a.startDate = $startDate,
                                        a.endDate = $endDate
                                    """,
                            Values.parameters(
                                    "id", activity.getId(),
                                    "name", activity.getName(),
                                    "description", description,
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

    private List<Float> parseEmbeddingVector(String str) {
        if (str == null || str.isBlank()) {
            return List.of();
        }

        str = str.replaceAll("[\\[\\]]", "");
        String[] tokens = str.split(",");
        List<Float> result = new ArrayList<>();
        for (String token : tokens) {
            result.add(Float.parseFloat(token.trim()));
        }
        return result;
    }

    @Transactional
    public void updateActivityEmbedding() {
        System.out.println("startttt");
        System.out.println(userRepository.updateActivityEmbeddingAvgForUsers());
    }
}
