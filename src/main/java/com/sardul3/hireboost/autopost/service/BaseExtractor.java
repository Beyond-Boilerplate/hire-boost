package com.sardul3.hireboost.autopost.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sardul3.hireboost.autopost.model.TrendingTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract non-sealed class BaseExtractor implements TrendingTopicService {

    private static final Logger logger = LoggerFactory.getLogger(BaseExtractor.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract String getApiUrl();
    protected abstract String getSourceName();

    @Override
    public CompletableFuture<List<TrendingTopic>> fetchTrendingTopics() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(getApiUrl()))
                        .header("User-Agent", "Mozilla/5.0")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                if (responseBody.trim().startsWith("{")) {
                    Map<String, Object> jsonMap = objectMapper.readValue(responseBody, new TypeReference<>() {});
                    return extractTopicsFromMap(jsonMap);
                } else if (responseBody.trim().startsWith("[")) {
                    List<Object> jsonArray = objectMapper.readValue(responseBody, new TypeReference<>() {});
                    return extractTopicsFromArray(jsonArray);
                } else {
                    logger.warn("Unexpected response format from {}", getSourceName());
                    return List.of();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted while fetching topics from {}", getSourceName(), e);
                return List.of();
            } catch (Exception e) {
                logger.error("Error fetching topics from {}", getSourceName(), e);
                return List.of();
            }
        });
    }

    protected abstract List<TrendingTopic> extractTopicsFromMap(Map<String, Object> jsonMap);

    protected List<TrendingTopic> extractTopicsFromArray(List<Object> jsonArray) {
        return List.of();
    }
}
