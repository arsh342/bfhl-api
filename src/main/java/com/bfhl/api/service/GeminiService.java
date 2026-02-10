package com.bfhl.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service that integrates with the Google Gemini API for AI-powered
 * question answering. Returns a single-word response.
 */
@Slf4j
@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT =
            "You are a concise answer engine. Answer the following question in exactly one word. " +
            "Only respond with a single word, nothing else. No punctuation, no explanation.";

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public GeminiService(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.url}") String apiUrl) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends a question to Google Gemini and returns a single-word answer.
     *
     * @param question the user's question
     * @return a single-word answer string
     */
    public String ask(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("AI question must not be empty");
        }

        // Sanitize input — strip HTML/script tags to prevent prompt injection
        String sanitized = question.replaceAll("<[^>]*>", "").trim();
        if (sanitized.length() > 500) {
            throw new IllegalArgumentException("AI question must not exceed 500 characters");
        }

        try {
            // Build the Gemini request payload
            Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                    "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                ),
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", sanitized)))
                ),
                "generationConfig", Map.of(
                    "temperature", 0.1,
                    "maxOutputTokens", 10
                )
            );

            String responseJson = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(REQUEST_TIMEOUT)
                    .block();

            return extractAnswer(responseJson);

        } catch (WebClientResponseException e) {
            log.error("Gemini API error: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI service returned an error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to call Gemini API", e);
            throw new RuntimeException("AI service is temporarily unavailable");
        }
    }

    /**
     * Extracts the text answer from Gemini's JSON response.
     */
    private String extractAnswer(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            String text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText("")
                    .trim();

            if (text.isEmpty()) {
                return "unknown";
            }

            // Return only the first word if Gemini returns multiple
            String[] words = text.split("\\s+");
            // Remove any trailing punctuation from the word
            return words[0].replaceAll("[^a-zA-Z0-9]", "").isEmpty()
                    ? text.split("\\s+")[0]
                    : words[0].replaceAll("[.,!?;:]$", "");

        } catch (Exception e) {
            log.warn("Failed to parse Gemini response, returning raw or fallback", e);
            return "unknown";
        }
    }
}
