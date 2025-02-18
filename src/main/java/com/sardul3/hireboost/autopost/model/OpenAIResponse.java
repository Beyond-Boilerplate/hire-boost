package com.sardul3.hireboost.autopost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Represents the response structure from OpenAI API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIResponse(List<Choice> choices) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(Message message) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String role, String content) {}
}
