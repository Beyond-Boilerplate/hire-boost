package com.sardul3.hireboost.autopost.service;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import com.sardul3.hireboost.autopost.service.BaseExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediumTopicExtractor extends BaseExtractor {

    private static final String RSS_FEED_URL = "https://medium.com/feed/tag/java";

    @Override
    protected String getApiUrl() {
        return RSS_FEED_URL;
    }

    @Override
    protected String getSourceName() {
        return "Medium";
    }

    @Override
    protected List<TrendingTopic> extractTopicsFromMap(java.util.Map<String, Object> jsonMap) {
        return List.of(); // Medium does not return JSON; we use XML parsing instead.
    }

    @Override
    protected List<TrendingTopic> extractTopicsFromArray(List<Object> jsonArray) {
        return List.of(); // Not used for Medium RSS parsing.
    }

    @Override
    public CompletableFuture<List<TrendingTopic>> fetchTrendingTopics() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // ✅ Fetch RSS XML feed
                Document doc = Jsoup.connect(RSS_FEED_URL).get();

                // ✅ Extract articles from `<item>` tags
                Elements items = doc.select("item");

                return items.stream()
                        .map(this::parseRssItem)
                        .limit(10) // Get top 10 trending articles
                        .collect(Collectors.toList());

            } catch (Exception e) {
                log.error("Failed to fetch or parse Medium RSS feed", e);
                return List.of();
            }
        });
    }

    /**
     * Parses an `<item>` element from the RSS feed into a `TrendingTopic` object.
     */
    private TrendingTopic parseRssItem(Element item) {
        String title = item.select("title").text();
        String link = item.select("link").text();
        String pubDate = item.select("pubDate").text();

        // ✅ Convert publication date to LocalDate
        LocalDate date = parsePubDate(pubDate);

        return new TrendingTopic(title, "Medium", date);
    }

    /**
     * Converts RSS `<pubDate>` format to `LocalDate`.
     */
    private LocalDate parsePubDate(String pubDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
            return LocalDate.parse(pubDate, formatter);
        } catch (Exception e) {
            log.warn("Failed to parse Medium pubDate: {}", pubDate);
            return LocalDate.now(); // Default to today if parsing fails
        }
    }
}
