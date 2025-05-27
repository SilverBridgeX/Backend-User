package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityType;
import com.example.silverbridgeX_user.activity.dto.ActivityResponseDto.FestivalItemDto;
import com.example.silverbridgeX_user.activity.repository.ActivityNativeRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FestivalApiService {

    private final ActivityRepository activityRepository;
    private final EmbeddingClient embeddingClient;
    private final ActivityNativeRepository activityNativeRepository;
    private final ActivityApiService activityApiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    String apiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api";

    public void fetchAndSaveFestivalData() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        int page = 1;
        while (true) {
            try {
                String urlStr = activityApiService.buildUrl(apiUrl, page);
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
}

