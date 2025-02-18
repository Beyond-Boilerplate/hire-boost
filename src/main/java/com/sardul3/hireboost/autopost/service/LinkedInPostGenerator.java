package com.sardul3.hireboost.autopost.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class LinkedInPostGenerator {

    private final AIService aiService;

    public LinkedInPostGenerator(AIService aiService) {
        this.aiService = aiService;
    }

    public CompletableFuture<String> generateLinkedInPost(String topic, String keyMessage, String targetAudience, String tone, String referenceLinks) {
        String prompt = """
            You are a social media strategist with over a decade of experience in crafting engaging LinkedIn posts that capture attention and encourage interaction.
            Your expertise lies in creating content that not only resonates with professionals but also has the potential to go viral, leveraging trending topics and effective hashtags.
           \s
            Your task is to generate a **LinkedIn post** about a specific topic.
           \s
            ### **🔹 Formatting Guidelines**
            - **Return the response as a structured JSON object** with the following keys:
                - `"intro"` → A brief opening line to capture attention.
                - `"desc"` → A short paragraph explaining the topic in an engaging way.
                - `"keyTakeAways"` → A list of 3-5 bullet points summarizing the key learnings.
                - `"links"` → A numbered list of 3-5 relevant free resource links.
                - `"cta"` → A closing sentence encouraging engagement.
                - `"hashTags"` → A space-separated string of 4-6 relevant hashtags.
            - **Do not add extra explanations, only return a valid JSON object**.
           \s
            ### **🔹 Post Details**
            - **Topic**: %s \s
            - **Key Message**: %s \s
            - **Target Audience**: %s \s
            - **Desired Tone**: %s \s
            - **Article Links to Reference**: %s \s

            Ensure the response is well-structured JSON **without any additional text**.
           \s""".formatted(topic, keyMessage, targetAudience, tone, referenceLinks);

        return aiService.generatePost(prompt);
    }


    /**
     * Generates a LinkedIn post with only a topic, using default values.
     * This method ensures exceptions are handled internally.
     */
    public String generateLinkedInPost(String topic) {
        String defaultKeyMessage = "This topic is shaping the future of software engineering, influencing how we build, scale, and innovate.";
        String defaultTargetAudience = "Software Engineers, CTOs, Product Managers, and Business Leaders";
        String defaultTone = "Engaging and Thought-Provoking";
        String defaultReferenceLinks = "https://medium.com/tag/software-development/latest";

        try {
            return generateLinkedInPost(topic, defaultKeyMessage, defaultTargetAudience, defaultTone, defaultReferenceLinks)
                    .get(); // Blocking call to retrieve result
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.error("Thread interrupted while generating LinkedIn post", e);
        } catch (ExecutionException e) {
            log.error("Error while generating LinkedIn post", e);
        }

        return "⚠️ Unable to generate a LinkedIn post at this moment. Please try again later.";
    }

}

