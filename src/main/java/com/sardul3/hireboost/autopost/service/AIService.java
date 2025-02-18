package com.sardul3.hireboost.autopost.service;

import java.util.concurrent.CompletableFuture;

public interface AIService {
    CompletableFuture<String> generatePost(String prompt);
}

