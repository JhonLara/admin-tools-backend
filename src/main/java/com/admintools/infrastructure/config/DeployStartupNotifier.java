package com.admintools.infrastructure.config;

import com.admintools.domain.service.DeployNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeployStartupNotifier implements ApplicationRunner {

    private final DeployNotificationService deployNotificationService;

    @Value("${app.version:desconocida}")
    private String appVersion;

    @Override
    public void run(ApplicationArguments args) {
        try {
            deployNotificationService.notifySuccess(appVersion);
            log.info("Notificaci\u00f3n de despliegue exitoso enviada (versi\u00f3n: {})", appVersion);
        } catch (Exception e) {
            log.error("No se pudo enviar notificaci\u00f3n de despliegue al iniciar", e);
        }
    }
}
