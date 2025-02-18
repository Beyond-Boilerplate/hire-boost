package com.sardul3.hireboost.autopost.service;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RedditTopicExtractor extends BaseExtractor {
    @Override
    protected String getApiUrl() {
        return "https://www.reddit.com/r/java/hot.json?limit=10";
    }

    @Override
    protected String getSourceName() {
        return "Reddit";
    }

    @Override
    protected List<TrendingTopic> extractTopicsFromMap(Map<String, Object> jsonMap) {
        Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
        if (data == null || !data.containsKey("children")) {
            log.warn("Reddit API response does not contain expected 'data.children' key");
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("children");

        return items.stream()
                .map(item -> {
                    Map<String, Object> itemData = (Map<String, Object>) item.get("data");
                    return new TrendingTopic((String) itemData.get("title"), "Reddit", LocalDate.now());
                })
                .limit(10)
                .toList();
    }
}
