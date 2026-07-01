package com.admintools.domain.service;

import com.admintools.domain.port.NotificationPort;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployNotificationService {

    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    public void notifySuccess(String version, String componente) {
        String chatId = telegramProperties.getSuperAdminChatId();
        if (chatId == null || chatId.isBlank()) {
            log.warn("No hay superAdminChatId configurado para notificaciones de despliegue");
            return;
        }

        String app = "BACKEND".equals(componente) ? "Admin Tools API (Backend)" : "Admin Tools Web (Frontend)";
        String message = String.format(
            "\u2705 *DESPLIEGUE EXITOSO*\n\n" +
            "Componente: %s\n" +
            "Se despleg\u00f3 exitosamente la versi\u00f3n *%s*",
            escapeMarkdown(app),
            escapeMarkdown(version)
        );

        try {
            notificationPort.sendMessage(chatId, message);
        } catch (Exception e) {
            log.error("No se pudo enviar notificaci\u00f3n de despliegue exitoso", e);
        }
    }

    public void notifyFailure(String version, String error, String componente) {
        String chatId = telegramProperties.getSuperAdminChatId();
        if (chatId == null || chatId.isBlank()) {
            log.warn("No hay superAdminChatId configurado para notificaciones de despliegue");
            return;
        }

        String app = "BACKEND".equals(componente) ? "Admin Tools API (Backend)" : "Admin Tools Web (Frontend)";
        String message = String.format(
            "\u274C *DESPLIEGUE FALLIDO*\n\n" +
            "Componente: %s\n" +
            "Fall\u00f3 el despliegue de la versi\u00f3n *%s*\n" +
            "Error: %s",
            escapeMarkdown(app),
            escapeMarkdown(version),
            escapeMarkdown(error != null ? error : "Error desconocido")
        );

        try {
            notificationPort.sendMessage(chatId, message);
        } catch (Exception e) {
            log.error("No se pudo enviar notificaci\u00f3n de despliegue fallido", e);
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
