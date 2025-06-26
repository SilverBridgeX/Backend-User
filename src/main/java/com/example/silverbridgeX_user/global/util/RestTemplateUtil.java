package com.example.silverbridgeX_user.global.util;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestTemplateUtil {

    private final RestTemplate restTemplate;

    public <T> T post(String url, Map<String, Object> parameters, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, headers);
        ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
        return response.getBody();
    }
}