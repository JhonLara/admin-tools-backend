package com.admintools.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    private boolean enabled = false;
    private String botToken;
    private String salesChatId;
    private String aliadosChatId;
}
