package com.arnav.taskscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClaudeApiConfig {

    // Injected from application.properties, which in turn reads the
    // CLAUDE_API_KEY environment variable. The key itself never lives in code.
    @Value("${claude.api.key}")
    private String claudeApiKey;

    @Value("${claude.api.url}")
    private String claudeApiUrl;

    @Bean
    public WebClient claudeWebClient() {
        return WebClient.builder()
                .baseUrl(claudeApiUrl)
                .defaultHeader("x-api-key", claudeApiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }
}
