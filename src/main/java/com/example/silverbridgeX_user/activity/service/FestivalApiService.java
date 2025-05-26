package com.example.silverbridgeX_user.activity.service;

import com.example.silverbridgeX_user.activity.domain.Activity;
import com.example.silverbridgeX_user.activity.domain.ActivityType;
import com.example.silverbridgeX_user.activity.dto.FestivalResponseDto.FestivalItemDto;
import com.example.silverbridgeX_user.activity.repository.ActivityNativeRepository;
import com.example.silverbridgeX_user.activity.repository.ActivityRepository;
import com.example.silverbridgeX_user.global.util.EmbeddingClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FestivalApiService {

    private final ActivityRepository activityRepository;
    private final EmbeddingClient embeddingClient;
    private final ActivityNativeRepository activityNativeRepository;

    @Value("${activity.api.key.festival}")
    private String apiKey;

    public void fetchAndSaveFestivalData() throws Exception {
        System.out.println("start");
        StringBuilder urlBuilder = new StringBuilder("http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8")
                + "=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
        urlBuilder.append("&type=json&pageNo=1&numOfRows=10");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        log.info("Raw JSON 응답:\n" + sb.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode items = objectMapper.readTree(sb.toString())
                .path("response")
                .path("body")
                .path("items");

        if (items != null && items.isArray()) {
            System.out.println("가져온 축제 개수: " + items.size());
            for (JsonNode item : items) {
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

                    String vectorLiteral = embeddingClient.getEmbeddingLiteral(
                            dto.getDescription());  // "(0.1, 0.2, ..., 0.3)"
                    activityNativeRepository.updateVectorByName(
                            dto.getName(),
                            vectorLiteral
                    );

                    log.info("저장됨: " + dto.getName());
                }


            }
        } else {
            log.info("items가 null이거나 배열이 아님. 실제 응답 확인 필요");
        }
    }
}

