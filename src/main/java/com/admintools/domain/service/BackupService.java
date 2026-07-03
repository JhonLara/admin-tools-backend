package com.admintools.domain.service;

import com.admintools.domain.model.*;
import com.admintools.domain.port.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupConfigRepositoryPort backupConfigRepository;
    private final BackupEjecucionRepositoryPort backupEjecucionRepository;
    private final BackupReporteRepositoryPort backupReporteRepository;
    private final MetricasMensualesRepositoryPort metricasMensualesRepository;
    private final SolicitudRepositoryPort solicitudRepository;
    private final HistorialNotificacionRepositoryPort historialRepository;
    private final SesionActivaRepositoryPort sesionRepository;
    private final EmpresaRepositoryPort empresaRepository;
    private final AliadoRepositoryPort aliadoRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void ejecutarBackupMensual() {
        log.info("Iniciando backup mensual programado");

        for (TipoBackup tipo : TipoBackup.values()) {
            Optional<BackupConfig> configOpt = backupConfigRepository.findByTipo(tipo);
            if (configOpt.isEmpty() || !configOpt.get().isActivo()) {
                log.info("Backup {} desactivado o no configurado, saltando", tipo);
                continue;
            }

            BackupConfig config = configOpt.get();
            ejecutarBackup(tipo, config);
        }

        log.info("Backup mensual programado finalizado");
    }

    @Transactional
    public void ejecutarBackupManual(TipoBackup tipo) {
        Optional<BackupConfig> configOpt = backupConfigRepository.findByTipo(tipo);
        if (configOpt.isEmpty()) {
            throw new IllegalStateException("No hay configuracion para backup tipo " + tipo);
        }
        ejecutarBackup(tipo, configOpt.get());
    }

    private void ejecutarBackup(TipoBackup tipo, BackupConfig config) {
        String periodo = calcularPeriodo();
        LocalDateTime inicio = LocalDate.parse(periodo + "-01").atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1);

        log.info("Ejecutando backup {} para periodo {} ({} - {})", tipo, periodo, inicio, fin);

        BackupEjecucion ejecucion = BackupEjecucion.builder()
                .tipo(tipo)
                .estado(EstadoBackup.EN_PROCESO)
                .periodo(periodo)
                .build();
        ejecucion = backupEjecucionRepository.save(ejecucion);

        try {
            int procesados = 0;

            switch (tipo) {
                case SOLICITUDES -> {
                    procesados = procesarSolicitudes(inicio, fin, config, periodo);
                }
                case HISTORIAL_NOTIFICACIONES -> {
                    procesados = procesarHistorialNotificaciones(inicio, fin, config, periodo);
                }
                case SESIONES -> {
                    procesados = procesarSesiones(inicio, fin, config, periodo);
                }
                case MONITOREO -> {
                    procesados = procesarMonitoreo(inicio, fin, config, periodo);
                }
            }

            ejecucion.setRegistrosProcesados(procesados);
            ejecucion.setEstado(EstadoBackup.EXITOSO);
            config.setUltimaEjecucion(LocalDateTime.now());
            config.setSiguienteEjecucion(LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(2).withMinute(0));
            backupConfigRepository.save(config);

            log.info("Backup {} para periodo {} completado: {} registros procesados", tipo, periodo, procesados);

        } catch (Exception e) {
            log.error("Error en backup {} para periodo {}", tipo, periodo, e);
            ejecucion.setEstado(EstadoBackup.FALLIDO);
            ejecucion.setMensajeError(e.getMessage());
        }

        backupEjecucionRepository.save(ejecucion);
    }

    private int procesarSolicitudes(LocalDateTime inicio, LocalDateTime fin, BackupConfig config, String periodo) throws IOException {
        List<Solicitud> solicitudes = solicitudRepository.findByFechaCreacionBetween(inicio, fin);
        int total = solicitudes.size();

        if (total == 0) {
            log.info("No hay solicitudes para el periodo {}", periodo);
            return 0;
        }

        if (config.isGenerarReporte()) {
            String csv = generarCsvSolicitudes(solicitudes);
            String path = guardarReporte("solicitudes", periodo, csv);
            log.info("Reporte de solicitudes guardado en {}", path);
            guardarReporteEnBaseDeDatos(TipoBackup.SOLICITUDES, periodo, "solicitudes_" + periodo + ".csv", csv, total);
        }

        Map<EstadoSolicitud, Long> porEstado = solicitudes.stream()
                .collect(Collectors.groupingBy(Solicitud::getEstado, Collectors.counting()));

        MetricasMensuales metricas = MetricasMensuales.builder()
                .periodo(periodo)
                .totalSolicitudes(total)
                .solicitudesPorEstado(objectMapper.writeValueAsString(porEstado))
                .build();
        metricasMensualesRepository.save(metricas);

        solicitudRepository.deleteByFechaCreacionBetween(inicio, fin);
        return total;
    }

    private int procesarHistorialNotificaciones(LocalDateTime inicio, LocalDateTime fin, BackupConfig config, String periodo) throws IOException {
        List<HistorialNotificacion> historial = historialRepository.findByFechaEnvioBetween(inicio, fin);
        int total = historial.size();

        if (total == 0) {
            log.info("No hay historial de notificaciones para el periodo {}", periodo);
            return 0;
        }

        if (config.isGenerarReporte()) {
            String csv = generarCsvHistorial(historial);
            String path = guardarReporte("historial_notificaciones", periodo, csv);
            log.info("Reporte de historial guardado en {}", path);
            guardarReporteEnBaseDeDatos(TipoBackup.HISTORIAL_NOTIFICACIONES, periodo, "historial_notificaciones_" + periodo + ".csv", csv, total);
        }

        historialRepository.deleteByFechaEnvioBetween(inicio, fin);
        return total;
    }

    private int procesarSesiones(LocalDateTime inicio, LocalDateTime fin, BackupConfig config, String periodo) throws IOException {
        List<SesionActiva> sesiones = sesionRepository.findByFechaInicioBetween(inicio, fin);
        int total = sesiones.size();

        if (total == 0) {
            log.info("No hay sesiones para el periodo {}", periodo);
            return 0;
        }

        if (config.isGenerarReporte()) {
            String csv = generarCsvSesiones(sesiones);
            String path = guardarReporte("sesiones", periodo, csv);
            log.info("Reporte de sesiones guardado en {}", path);
            guardarReporteEnBaseDeDatos(TipoBackup.SESIONES, periodo, "sesiones_" + periodo + ".csv", csv, total);
        }

        sesionRepository.deleteByFechaInicioBetween(inicio, fin);
        return total;
    }

    private int procesarMonitoreo(LocalDateTime inicio, LocalDateTime fin, BackupConfig config, String periodo) {
        int totalEmpresas = (int) empresaRepository.count();
        int totalAliados = (int) aliadoRepository.count();

        MetricasMensuales existentes = metricasMensualesRepository.findByPeriodo(periodo).orElse(null);
        MetricasMensuales metricas;
        if (existentes != null) {
            metricas = existentes;
            metricas.setTotalEmpresas(totalEmpresas);
            metricas.setTotalAliados(totalAliados);
        } else {
            metricas = MetricasMensuales.builder()
                    .periodo(periodo)
                    .totalEmpresas(totalEmpresas)
                    .totalAliados(totalAliados)
                    .build();
        }
        metricasMensualesRepository.save(metricas);

        log.info("Monitoreo procesado para periodo {}: {} empresas, {} aliados", periodo, totalEmpresas, totalAliados);
        return totalEmpresas + totalAliados;
    }

    private String calcularPeriodo() {
        return LocalDate.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private String generarCsvSolicitudes(List<Solicitud> solicitudes) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,cedula_cliente,aliado,empresa,analista,estado,creado_por,fecha_creacion,fecha_asignacion,fecha_finalizacion\n");
        for (Solicitud s : solicitudes) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    s.getId(),
                    escapeCsv(s.getCedulaCliente()),
                    s.getAliado() != null ? escapeCsv(s.getAliado().getNombre()) : "",
                    s.getEmpresa() != null ? escapeCsv(s.getEmpresa().getNombre()) : "",
                    s.getAnalista() != null ? escapeCsv(s.getAnalista().getNombre()) : "",
                    s.getEstado(),
                    escapeCsv(s.getCreadoPor()),
                    s.getFechaCreacion(),
                    s.getFechaAsignacion(),
                    s.getFechaFinalizacion()));
        }
        return sb.toString();
    }

    private String generarCsvHistorial(List<HistorialNotificacion> historial) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,solicitud_id,canal,origen,destino,mensaje_enviado,estado_envio,fecha_envio\n");
        for (HistorialNotificacion h : historial) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    h.getId(),
                    h.getSolicitud() != null ? h.getSolicitud().getId() : "",
                    h.getCanal(),
                    h.getOrigen(),
                    h.getDestino(),
                    escapeCsv(h.getMensajeEnviado()),
                    h.getEstadoEnvio(),
                    h.getFechaEnvio()));
        }
        return sb.toString();
    }

    private String generarCsvSesiones(List<SesionActiva> sesiones) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,username,nombre,rol,ip_address,user_agent,fecha_inicio,fecha_expiracion,activa\n");
        for (SesionActiva s : sesiones) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    s.getId(),
                    escapeCsv(s.getUsername()),
                    escapeCsv(s.getNombre()),
                    s.getRol(),
                    escapeCsv(s.getIpAddress()),
                    escapeCsv(s.getUserAgent()),
                    s.getFechaInicio(),
                    s.getFechaExpiracion(),
                    s.getActiva()));
        }
        return sb.toString();
    }

    private String guardarReporte(String tipo, String periodo, String contenido) throws IOException {
        String dir = System.getProperty("user.dir") + "/backups";
        Path pathDir = Paths.get(dir);
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }
        String filename = String.format("%s_%s.csv", tipo, periodo);
        Path filePath = pathDir.resolve(filename);
        Files.writeString(filePath, contenido);
        return filePath.toAbsolutePath().toString();
    }

    private void guardarReporteEnBaseDeDatos(TipoBackup tipo, String periodo, String nombreArchivo, String contenido, int registrosProcesados) {
        BackupReporte reporte = BackupReporte.builder()
                .tipo(tipo)
                .periodo(periodo)
                .nombreArchivo(nombreArchivo)
                .contenido(contenido)
                .registrosProcesados(registrosProcesados)
                .tamanoBytes(contenido.getBytes().length)
                .build();
        backupReporteRepository.save(reporte);
        log.info("Reporte persistido en base de datos: {} periodo {}, {} bytes", nombreArchivo, periodo, contenido.getBytes().length);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }
}
