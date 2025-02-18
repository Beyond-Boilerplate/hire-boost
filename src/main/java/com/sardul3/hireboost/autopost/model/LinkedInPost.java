package com.sardul3.hireboost.autopost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LinkedInPost(
        String intro,
        String desc,
        List<String> keyTakeAways,
        List<String> links,
        String cta,
        String hashTags
) {}

