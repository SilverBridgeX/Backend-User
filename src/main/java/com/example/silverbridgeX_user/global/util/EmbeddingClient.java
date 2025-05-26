package com.example.silverbridgeX_user.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingClient {

    public String getEmbeddingLiteral(String text) throws Exception {
        URL url = new URL("http://localhost:8001/embed");
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
}