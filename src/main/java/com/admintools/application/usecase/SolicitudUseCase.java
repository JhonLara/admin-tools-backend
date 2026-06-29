package com.admintools.application.usecase;

import com.admintools.application.dto.CrearSolicitudRequest;
import com.admintools.application.dto.SolicitudResponse;
import com.admintools.domain.model.*;
import com.admintools.domain.port.*;
import com.admintools.domain.service.AsignacionService;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final AliadoRepositoryPort aliadoRepository;
    private final HistorialNotificacionRepositoryPort historialRepository;
    private final AsignacionService asignacionService;
    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    public SolicitudResponse crearSolicitud(CrearSolicitudRequest request) {
        Aliado aliado = aliadoRepository.findById(request.getAliadoId())
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));

        Solicitud solicitud = Solicitud.builder()
                .cedulaCliente(request.getCedulaCliente())
                .aliado(aliado)
                .estado(EstadoSolicitud.CREADA)
                .build();

        solicitud = solicitudRepository.save(solicitud);

        solicitud = asignacionService.asignarSolicitud(solicitud);

        String mensaje = String.format(
                "Nueva solicitud asignada\nCliente: %s\nEmpresa: %s\nAliado: %s\nAnalista: %s",
                solicitud.getCedulaCliente(),
                aliado.getEmpresa().getNombre(),
                aliado.getNombre(),
                solicitud.getAnalista().getNombre()
        );

        String respuesta = notificationPort.sendMessage(telegramProperties.getSalesChatId(), mensaje);

        HistorialNotificacion historial = HistorialNotificacion.builder()
                .solicitud(solicitud)
                .canal(Canal.TELEGRAM)
                .origen(Origen.VENDEDOR)
                .destino(Destino.GRUPO_ANALISTAS)
                .mensajeEnviado(mensaje)
                .estadoEnvio(respuesta.contains("error") || respuesta.startsWith("Error:") ? EstadoEnvio.ERROR : EstadoEnvio.ENVIADO)
                .respuestaIntegracion(respuesta)
                .build();

        historialRepository.save(historial);

        return mapToResponse(solicitud);
    }

    public SolicitudResponse notificarObservacion(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        Aliado aliado = solicitud.getAliado();
        String chatId = aliado.getTelegramChatId();
        if (chatId == null || chatId.isBlank()) {
            chatId = telegramProperties.getAliadosChatId();
        }

        String mensaje = String.format(
                "Se registró una observación en tu solicitud\nCliente: %s\nEmpresa: %s\nAliado: %s\nPor favor revisar",
                solicitud.getCedulaCliente(),
                aliado.getEmpresa().getNombre(),
                aliado.getNombre()
        );

        String respuesta = notificationPort.sendMessage(chatId, mensaje);

        HistorialNotificacion historial = HistorialNotificacion.builder()
                .solicitud(solicitud)
                .canal(Canal.TELEGRAM)
                .origen(Origen.ANALISTA)
                .destino(Destino.GRUPO_ALIADO)
                .mensajeEnviado(mensaje)
                .estadoEnvio(respuesta.contains("error") || respuesta.startsWith("Error:") ? EstadoEnvio.ERROR : EstadoEnvio.ENVIADO)
                .respuestaIntegracion(respuesta)
                .build();

        historialRepository.save(historial);

        if (!respuesta.startsWith("Error:")) {
            solicitud.setEstado(EstadoSolicitud.NOTIFICADA);
            solicitudRepository.save(solicitud);
        }

        return mapToResponse(solicitud);
    }

    public SolicitudResponse rechazarSolicitud(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud = solicitudRepository.save(solicitud);

        enviarNotificacionEstado(solicitud, "RECHAZADA");

        return mapToResponse(solicitud);
    }

    public SolicitudResponse aprobarSolicitud(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud = solicitudRepository.save(solicitud);

        enviarNotificacionEstado(solicitud, "APROBADA");

        return mapToResponse(solicitud);
    }

    private void enviarNotificacionEstado(Solicitud solicitud, String estado) {
        Aliado aliado = solicitud.getAliado();
        String chatId = aliado.getTelegramChatId();
        if (chatId == null || chatId.isBlank()) {
            chatId = telegramProperties.getAliadosChatId();
        }

        String mensaje = String.format(
                "SOLICITUD %s\nCliente: %s\nEmpresa: %s\nAliado: %s%s",
                estado,
                solicitud.getCedulaCliente(),
                aliado.getEmpresa().getNombre(),
                aliado.getNombre(),
                "APROBADA".equals(estado) ? "\n\nSe puede enrolar" : ""
        );

        String respuesta = notificationPort.sendMessage(chatId, mensaje);

        HistorialNotificacion historial = HistorialNotificacion.builder()
                .solicitud(solicitud)
                .canal(Canal.TELEGRAM)
                .origen(Origen.ANALISTA)
                .destino(Destino.GRUPO_ALIADO)
                .mensajeEnviado(mensaje)
                .estadoEnvio(respuesta.contains("error") || respuesta.startsWith("Error:") ? EstadoEnvio.ERROR : EstadoEnvio.ENVIADO)
                .respuestaIntegracion(respuesta)
                .build();

        historialRepository.save(historial);
    }

    public SolicitudResponse finalizarSolicitud(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.FINALIZADA);
        solicitud.setFechaFinalizacion(LocalDateTime.now());
        solicitud = solicitudRepository.save(solicitud);

        return mapToResponse(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarSolicitudes() {
        return solicitudRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarSolicitudesPorAnalista(UUID analistaId) {
        return solicitudRepository.findByAnalistaId(analistaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SolicitudResponse obtenerSolicitud(UUID id) {
        return solicitudRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
    }

    private SolicitudResponse mapToResponse(Solicitud s) {
        return SolicitudResponse.builder()
                .id(s.getId())
                .cedulaCliente(s.getCedulaCliente())
                .aliado(SolicitudResponse.AliadoResumen.builder()
                        .id(s.getAliado().getId())
                        .nombre(s.getAliado().getNombre())
                        .build())
                .analista(s.getAnalista() != null ? SolicitudResponse.AnalistaResumen.builder()
                        .id(s.getAnalista().getId())
                        .nombre(s.getAnalista().getNombre())
                        .cedula(s.getAnalista().getCedula())
                        .build() : null)
                .estado(s.getEstado())
                .fechaCreacion(s.getFechaCreacion())
                .fechaAsignacion(s.getFechaAsignacion())
                .fechaFinalizacion(s.getFechaFinalizacion())
                .build();
    }
}
