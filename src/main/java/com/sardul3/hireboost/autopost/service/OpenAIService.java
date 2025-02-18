package com.sardul3.hireboost.autopost.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sardul3.hireboost.autopost.model.LinkedInPost;
import com.sardul3.hireboost.autopost.model.OpenAIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OpenAIService implements AIService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Override
    public CompletableFuture<String> generatePost(String prompt) {
        return CompletableFuture.supplyAsync(() -> callOpenAiApi(prompt)) // Make API call asynchronously
                .exceptionally(this::handleError); //≈ Handle errors cleanly
    }


    /**
     * Calls OpenAI API and returns the raw response body.
     */
    private String callOpenAiApi(String prompt) {
        try {
            HttpEntity<Map<String, Object>> request = buildHttpRequest(prompt);
            OpenAIResponse openAiResponse = sendRequest(request);

            return parseAndFormatResponse(openAiResponse); // ✅ Now passing OpenAIResponse object
        } catch (RestClientException e) {
            log.error("Error calling OpenAI API", e);
            return "⚠️ Unable to reach OpenAI API. Please check your network or API key.";
        } catch (Exception e) {
            log.error("Unexpected error while generating LinkedIn post", e);
            return "⚠️ An unexpected error occurred. Please try again later.";
        }
    }


    private HttpEntity<Map<String, Object>> buildHttpRequest(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "logit_bias", Map.of("198", -100) // Discourage newlines
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(requestBody, headers);
    }

    private OpenAIResponse sendRequest(HttpEntity<Map<String, Object>> request) {
        ResponseEntity<OpenAIResponse> responseEntity = restTemplate.exchange(
                openAiApiUrl,
                HttpMethod.POST,
                request,
                OpenAIResponse.class
        );

        return Objects.requireNonNull(responseEntity.getBody());
    }


    private String parseAndFormatResponse(OpenAIResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            log.error("Invalid OpenAI response: Missing or empty 'choices' array");
            return "⚠️ AI service returned an empty response.";
        }

        try {
            String contentJson = response.choices().get(0).message().content();
            LinkedInPost post = OBJECT_MAPPER.readValue(contentJson, LinkedInPost.class);
            return formatLinkedInPost(post);
        } catch (JsonProcessingException e) {
            log.error("Error parsing LinkedInPost JSON", e);
            return "⚠️ Unable to process AI response.";
        }
    }


    private String formatLinkedInPost(LinkedInPost post) {
        StringBuilder formattedPost = new StringBuilder();

        formattedPost.append(post.intro()).append("<br /><br />");
        formattedPost.append(post.desc()).append("<br /><br />");

        formattedPost.append("💡 Key Takeaways:<br />");
        post.keyTakeAways().forEach(point -> formattedPost.append("- ").append(point).append("<br />"));
        formattedPost.append("<br />");

        formattedPost.append("🔗 Learn More:<br />");
        post.links().forEach(link -> formattedPost.append(link).append("<br />"));
        formattedPost.append("<br />");

        formattedPost.append(post.cta()).append("<br /><br />");
        formattedPost.append(post.hashTags());

        return formattedPost.toString();
    }


    /**
     * Extracts AI-generated text from the OpenAI response.
     */
    private LinkedInPost parseResponse(String responseBody) {
        try {
            return OBJECT_MAPPER.readValue(responseBody, LinkedInPost.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing OpenAI response JSON", e);
            throw new RuntimeException("⚠️ Error processing OpenAI response.");
        }
    }


    /**
     * Handles API call failures and returns a fallback message.
     */
    private String handleError(Throwable e) {
        log.error("Error generating LinkedIn post", e);
        return "⚠️ Unable to generate a LinkedIn post at this moment. Please try again later.";
    }
}
