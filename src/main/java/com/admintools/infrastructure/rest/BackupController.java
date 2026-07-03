package com.admintools.infrastructure.rest;

import com.admintools.application.dto.BackupReporteResponse;
import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.BackupEjecucion;
import com.admintools.domain.model.BackupReporte;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupConfigRepositoryPort;
import com.admintools.domain.port.BackupEjecucionRepositoryPort;
import com.admintools.domain.port.BackupReporteRepositoryPort;
import com.admintools.domain.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupConfigRepositoryPort backupConfigRepository;
    private final BackupEjecucionRepositoryPort backupEjecucionRepository;
    private final BackupReporteRepositoryPort backupReporteRepository;
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
    public ResponseEntity<Map<String, Object>> ejecutarManual(@PathVariable String tipo, @RequestBody(required = false) EjecutarBackupRequest request) {
        TipoBackup tipoBackup = TipoBackup.valueOf(tipo.toUpperCase());
        String periodo = request != null ? request.periodo() : null;
        backupService.ejecutarBackupManual(tipoBackup, periodo);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Backup manual iniciado",
                "tipo", tipoBackup.name(),
                "periodo", periodo != null ? periodo : "default",
                "fecha", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/ejecuciones")
    public ResponseEntity<List<BackupEjecucion>> listarEjecuciones() {
        return ResponseEntity.ok(backupEjecucionRepository.findTop10ByOrderByFechaEjecucionDesc());
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<BackupReporteResponse>> listarReportes() {
        List<BackupReporteResponse> reportes = backupReporteRepository.findAll().stream()
                .map(r -> BackupReporteResponse.builder()
                        .id(r.getId())
                        .tipo(r.getTipo())
                        .periodo(r.getPeriodo())
                        .nombreArchivo(r.getNombreArchivo())
                        .registrosProcesados(r.getRegistrosProcesados())
                        .tamanoBytes(r.getTamanoBytes())
                        .fechaGeneracion(r.getFechaGeneracion())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/reportes/{id}/descargar")
    public ResponseEntity<byte[]> descargarReporte(@PathVariable UUID id) {
        BackupReporte reporte = backupReporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        byte[] contenido = reporte.getContenido().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", reporte.getNombreArchivo());

        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido);
    }

    @DeleteMapping("/reportes/{id}")
    public ResponseEntity<Map<String, String>> eliminarReporte(@PathVariable UUID id) {
        BackupReporte reporte = backupReporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        backupReporteRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Reporte eliminado",
                "archivo", reporte.getNombreArchivo()
        ));
    }

    public record EjecutarBackupRequest(String periodo) {}

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
