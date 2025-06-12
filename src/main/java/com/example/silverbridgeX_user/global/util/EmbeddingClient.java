package com.example.silverbridgeX_user.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingClient {

    public String getEmbeddingLiteral(String text) throws Exception {
        URL url = new URL("http://localhost:8000/embedding");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = "{\"text\": \"" + text.replace("\"", "\\\"") + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
            os.flush();
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();

        JsonNode embeddingNode = new ObjectMapper().readTree(response).get("embedding");
        double[] array = new ObjectMapper().convertValue(embeddingNode, double[].class);
        return "[" + DoubleStream.of(array)
                .mapToObj(Double::toString)
                .collect(Collectors.joining(", ")) + "]";
    }

    public String getMaxPooledEmbeddingLiteral(List<String> keywords) throws Exception {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        // 각 키워드 임베딩 추출
        double[][] embeddings = new double[keywords.size()][];

        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);
            String vectorStr = getEmbeddingLiteral(keyword); // "[0.1, 0.2, ...]"
            String[] parts = vectorStr.replace("[", "").replace("]", "").split(",");
            embeddings[i] = new double[parts.length];
            for (int j = 0; j < parts.length; j++) {
                embeddings[i][j] = Double.parseDouble(parts[j].trim());
            }
        }

        // max pooling 계산
        int dim = embeddings[0].length;
        double[] maxPooled = new double[dim];
        for (int d = 0; d < dim; d++) {
            double max = Double.NEGATIVE_INFINITY;
            for (double[] vec : embeddings) {
                max = Math.max(max, vec[d]);
            }
            maxPooled[d] = max;
        }

        // PostgreSQL vector 타입용 문자열 생성
        return "[" + DoubleStream.of(maxPooled)
                .mapToObj(d -> String.format("%.6f", d))
                .collect(Collectors.joining(", ")) + "]";
    }

}