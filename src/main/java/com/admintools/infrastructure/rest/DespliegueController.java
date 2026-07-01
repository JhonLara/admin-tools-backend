package com.admintools.infrastructure.rest;

import com.admintools.domain.service.DeployNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/despliegue")
@RequiredArgsConstructor
public class DespliegueController {

    private final DeployNotificationService deployNotificationService;

    @PostMapping("/notificar")
    public ResponseEntity<Map<String, Object>> notificarDespliegue(@RequestBody NotificarDespliegueRequest request) {
        if (request.isExitoso()) {
            deployNotificationService.notifySuccess(request.getVersion());
        } else {
            deployNotificationService.notifyFailure(request.getVersion(), request.getError());
        }
        return ResponseEntity.ok(Map.of(
            "mensaje", "Notificaci\u00f3n enviada",
            "version", request.getVersion(),
            "estado", request.isExitoso() ? "EXITOSO" : "FALLIDO",
            "fecha", LocalDateTime.now().toString()
        ));
    }

    public static class NotificarDespliegueRequest {
        private String version;
        private boolean exitoso;
        private String error;

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isExitoso() { return exitoso; }
        public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
