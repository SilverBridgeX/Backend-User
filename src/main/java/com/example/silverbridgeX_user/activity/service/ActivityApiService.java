package com.example.silverbridgeX_user.activity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivityApiService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${activity.api.key.festival}")
    private String apiKey;

    public String buildUrl(String url, int pageNo) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(apiKey,
                StandardCharsets.UTF_8));
        urlBuilder.append("&type=json&pageNo=" + pageNo + "&numOfRows=50");
        return urlBuilder.toString();
    }

    public String getJsonFromUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
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

        return sb.toString();
    }

    public JsonNode parseItems(String json) throws Exception {
        return objectMapper.readTree(json)
                .path("response")
                .path("body")
                .path("items");
    }

    public JsonNode parsePoint(String json) throws Exception {
        return objectMapper.readTree(json)
                .path("response")
                .path("result")
                .path("point");
    }
}
