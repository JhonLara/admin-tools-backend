package com.admintools.infrastructure.config;

import com.admintools.domain.service.DeployNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeployStartupNotifier implements ApplicationRunner {

    private final DeployNotificationService deployNotificationService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            String version = System.getProperty("app.version", System.getenv("APP_VERSION"));
            if (version == null || version.isBlank()) {
                version = "desconocida";
            }
            deployNotificationService.notifySuccess(version);
            log.info("Notificaci\u00f3n de despliegue exitoso enviada (versi\u00f3n: {})", version);
        } catch (Exception e) {
            log.error("No se pudo enviar notificaci\u00f3n de despliegue al iniciar", e);
        }
    }
}
