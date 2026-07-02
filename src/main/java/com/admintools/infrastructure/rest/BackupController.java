package com.admintools.infrastructure.rest;

import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.BackupEjecucion;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupConfigRepositoryPort;
import com.admintools.domain.port.BackupEjecucionRepositoryPort;
import com.admintools.domain.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupConfigRepositoryPort backupConfigRepository;
    private final BackupEjecucionRepositoryPort backupEjecucionRepository;
    private final BackupService backupService;

    @GetMapping("/config")
    public ResponseEntity<List<BackupConfig>> listarConfig() {
        return ResponseEntity.ok(backupConfigRepository.findAll());
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<BackupConfig> actualizarConfig(@PathVariable UUID id, @RequestBody BackupConfigRequest request) {
        BackupConfig config = backupConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuracion no encontrada"));
        config.setActivo(request.isActivo());
        config.setRetencionDias(request.getRetencionDias());
        config.setGenerarReporte(request.isGenerarReporte());
        config.setDestinoReporte(request.getDestinoReporte());
        return ResponseEntity.ok(backupConfigRepository.save(config));
    }

    @PostMapping("/ejecutar/{tipo}")
    public ResponseEntity<Map<String, Object>> ejecutarManual(@PathVariable String tipo) {
        TipoBackup tipoBackup = TipoBackup.valueOf(tipo.toUpperCase());
        backupService.ejecutarBackupManual(tipoBackup);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Backup manual iniciado",
                "tipo", tipoBackup.name(),
                "fecha", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/ejecuciones")
    public ResponseEntity<List<BackupEjecucion>> listarEjecuciones() {
        return ResponseEntity.ok(backupEjecucionRepository.findTop10ByOrderByFechaEjecucionDesc());
    }

    public static class BackupConfigRequest {
        private boolean activo;
        private int retencionDias;
        private boolean generarReporte;
        private String destinoReporte;

        public boolean isActivo() { return activo; }
        public void setActivo(boolean activo) { this.activo = activo; }
        public int getRetencionDias() { return retencionDias; }
        public void setRetencionDias(int retencionDias) { this.retencionDias = retencionDias; }
        public boolean isGenerarReporte() { return generarReporte; }
        public void setGenerarReporte(boolean generarReporte) { this.generarReporte = generarReporte; }
        public String getDestinoReporte() { return destinoReporte; }
        public void setDestinoReporte(String destinoReporte) { this.destinoReporte = destinoReporte; }
    }
}
