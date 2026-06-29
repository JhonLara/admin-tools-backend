package com.admintools.infrastructure.external;

import com.admintools.domain.port.NotificationPort;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationAdapter implements NotificationPort {

    private final TelegramProperties properties;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String sendMessage(String chatId, String message) {
        if (!properties.isEnabled()) {
            log.info("Telegram disabled. Simulated send to chat {}: {}", chatId, message);
            return "Telegram disabled - simulated success";
        }

        try {
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    properties.getBotToken()
            );

            String jsonBody = String.format(
                    "{\"chat_id\":\"%s\",\"text\":\"%s\"}",
                    chatId,
                    escapeJson(message)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Telegram API response: status={}, body={}", response.statusCode(), response.body());
            return response.body();
        } catch (Exception e) {
            log.error("Error sending Telegram message", e);
            return "Error: " + e.getMessage();
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
