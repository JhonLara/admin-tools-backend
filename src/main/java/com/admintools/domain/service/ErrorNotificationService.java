package com.admintools.domain.service;

import com.admintools.domain.port.NotificationPort;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorNotificationService {

    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    public void notifyError(String context, Exception exception) {
        String chatId = telegramProperties.getErrorsChatId();
        if (chatId == null || chatId.isBlank()) {
            log.warn("No hay errorsChatId configurado para notificaciones de error");
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("\u274C *ERROR EN ADMIN TOOLS*\n\n");
        message.append("*Contexto:* ").append(escapeMarkdown(context)).append("\n");
        message.append("*Mensaje:* ").append(escapeMarkdown(exception.getMessage() != null ? exception.getMessage() : "Sin mensaje")).append("\n");
        message.append("*Clase:* ").append(exception.getClass().getSimpleName()).append("\n");

        if (exception.getStackTrace().length > 0) {
            message.append("*Ubicación:* ").append(exception.getStackTrace()[0].toString()).append("\n");
        }

        try {
            notificationPort.sendMessage(chatId, message.toString());
        } catch (Exception e) {
            log.error("No se pudo enviar notificación de error a Telegram", e);
        }
    }

    public void notifyInconsistency(String description) {
        String chatId = telegramProperties.getErrorsChatId();
        if (chatId == null || chatId.isBlank()) {
            log.warn("No hay errorsChatId configurado para notificaciones de inconsistencia");
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("\u26A0\uFE0F *INCONSISTENCIA EN ADMIN TOOLS*\n\n");
        message.append("*Descripción:* ").append(escapeMarkdown(description)).append("\n");

        try {
            notificationPort.sendMessage(chatId, message.toString());
        } catch (Exception e) {
            log.error("No se pudo enviar notificación de inconsistencia a Telegram", e);
        }
    }

    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }
}
