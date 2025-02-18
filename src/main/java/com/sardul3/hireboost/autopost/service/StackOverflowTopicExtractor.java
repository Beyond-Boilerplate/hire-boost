package com.sardul3.hireboost.autopost.service.extractors;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import com.sardul3.hireboost.autopost.service.BaseExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StackOverflowTopicExtractor extends BaseExtractor {

    @Value("${stackoverflow.key}")
    private String API_KEY;
    @Override
    protected String getApiUrl() {
        return "https://api.stackexchange.com/2.3/questions?order=desc&sort=votes&tagged=java;spring-boot&site=stackoverflow";
    }

    @Override
    protected String getSourceName() {
        return "Stack Overflow";
    }

    @Override
    protected List<TrendingTopic> extractTopicsFromMap(Map<String, Object> jsonMap) {
        // ✅ Ensure "items" key exists
        Object itemsObject = jsonMap.get("items");
        if (!(itemsObject instanceof List<?> items)) {
            log.warn("Stack Overflow API response does not contain expected 'items' key");
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) items;

        return itemsList.stream()
                .map(item -> new TrendingTopic(
                        (String) item.get("title"), // ✅ Extracts the question title
                        "Stack Overflow",
                        LocalDate.now()
                ))
                .limit(10) // ✅ Limits results to 10
                .toList();
    }
}
