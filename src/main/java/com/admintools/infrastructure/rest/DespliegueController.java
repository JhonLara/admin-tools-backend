package com.admintools.infrastructure.rest;

import com.admintools.domain.service.DeployNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/despliegue")
@RequiredArgsConstructor
public class DespliegueController {

    private final DeployNotificationService deployNotificationService;

    @Value("${app.version:desconocida}")
    private String appVersion;

    @PostMapping("/notificar")
    public ResponseEntity<Map<String, Object>> notificarDespliegue(@RequestBody NotificarDespliegueRequest request) {
        String version = request.getVersion() != null && !request.getVersion().isBlank()
                ? request.getVersion() : appVersion;
        String componente = request.getComponente() != null && !request.getComponente().isBlank()
                ? request.getComponente() : "BACKEND";
        if (request.isExitoso()) {
            deployNotificationService.notifySuccess(version, componente);
        } else {
            deployNotificationService.notifyFailure(version, request.getError(), componente);
        }
        return ResponseEntity.ok(Map.of(
            "mensaje", "Notificaci\u00f3n enviada",
            "version", version,
            "componente", componente,
            "estado", request.isExitoso() ? "EXITOSO" : "FALLIDO",
            "fecha", LocalDateTime.now().toString()
        ));
    }

    public static class NotificarDespliegueRequest {
        private String version;
        private boolean exitoso;
        private String error;
        private String componente;

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isExitoso() { return exitoso; }
        public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getComponente() { return componente; }
        public void setComponente(String componente) { this.componente = componente; }
    }
}
