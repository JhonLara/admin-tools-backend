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
    private final EmpresaRepositoryPort empresaRepository;
    private final AliadoEmpresaTelegramRepositoryPort aliadoEmpresaTelegramRepository;
    private final HistorialNotificacionRepositoryPort historialRepository;
    private final AsignacionService asignacionService;
    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    private String obtenerChatId(Aliado aliado, Empresa empresa) {
        return aliadoEmpresaTelegramRepository.findByAliadoId(aliado.getId())
                .map(AliadoEmpresaTelegram::getTelegramChatId)
                .filter(chatId -> chatId != null && !chatId.isBlank())
                .orElseGet(() -> {
                    String chatId = aliado.getTelegramChatId();
                    return (chatId != null && !chatId.isBlank()) ? chatId : telegramProperties.getAliadosChatId();
                });
    }

    public SolicitudResponse crearSolicitud(CrearSolicitudRequest request, String creadoPor) {
        Aliado aliado = aliadoRepository.findById(request.getAliadoId())
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        boolean empresaPermitida = aliado.getEmpresas().stream()
                .anyMatch(e -> e.getId().equals(empresa.getId()));
        if (!empresaPermitida) {
            throw new IllegalArgumentException("La empresa seleccionada no está relacionada con el aliado");
        }

        Solicitud solicitud = Solicitud.builder()
                .cedulaCliente(request.getCedulaCliente())
                .aliado(aliado)
                .empresa(empresa)
                .creadoPor(creadoPor)
                .estado(EstadoSolicitud.CREADA)
                .build();

        solicitud = solicitudRepository.save(solicitud);

        solicitud = asignacionService.asignarSolicitud(solicitud);

        String mensaje = String.format(
                "Nueva solicitud asignada\nCliente: %s\nEmpresa: %s\nAliado: %s\nAnalista: %s",
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
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
        String chatId = obtenerChatId(aliado, solicitud.getEmpresa());

        String mensaje = String.format(
                "Se registró una observación en tu solicitud\nCliente: %s\nEmpresa: %s\nAliado: %s\nPor favor revisar",
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
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

    public SolicitudResponse validarSolicitud(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        Aliado aliado = solicitud.getAliado();
        String chatId = obtenerChatId(aliado, solicitud.getEmpresa());

        String mensaje = String.format(
                "Solicitud validada\ncliente: %s\nEmpresa: %s\nAliado: %s\n\nSe ha enviado enlace para firma digital📱. \n\nRequisitos:\n•Foto Legible 📸: Asegúrese de que la foto sea clara y visible, con el rostro descubierto:\n•Con cédula en mano visible y legible 🪪\n•No usar gafas 🕶️\n•No usar gorras o sombreros 🧢\n•No usar mascarillas o bufandas 😷\n\nProceso de Firma 📝\nUna vez que el cliente haya firmado el documento, confirmar al analista para aprobación de la solicitud y autorización del enrolamiento y entrega del equipo.",
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
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

        solicitud.setEstado(EstadoSolicitud.VALIDADA);
        solicitud = solicitudRepository.save(solicitud);

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
        String chatId = obtenerChatId(aliado, solicitud.getEmpresa());

        String mensaje = String.format(
                "SOLICITUD %s\nCliente: %s\nEmpresa: %s\nAliado: %s%s",
                estado,
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
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
                .sorted((a, b) -> b.getFechaActualizacion().compareTo(a.getFechaActualizacion()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarSolicitudesPorAnalista(UUID analistaId) {
        return solicitudRepository.findByAnalistaId(analistaId).stream()
                .filter(s -> s.getEstado() != EstadoSolicitud.APROBADA && s.getEstado() != EstadoSolicitud.RECHAZADA)
                .sorted((a, b) -> b.getFechaActualizacion().compareTo(a.getFechaActualizacion()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarSolicitudesPorVendedor(String username) {
        return solicitudRepository.findByCreadoPor(username).stream()
                .sorted((a, b) -> b.getFechaActualizacion().compareTo(a.getFechaActualizacion()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SolicitudResponse marcarFirmaRecibida(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.VALIDADA) {
            throw new IllegalArgumentException("La solicitud debe estar en estado VALIDADA para marcar firma recibida");
        }

        String mensaje = null;
        Analista analistaActual = solicitud.getAnalista();
        if (analistaActual != null && !asignacionService.estaEnTurno(analistaActual.getId())) {
            java.util.Optional<Analista> nuevoOpt = asignacionService.buscarAnalistaEnTurnoExcluyendo(analistaActual.getId());
            if (nuevoOpt.isPresent()) {
                Analista nuevo = nuevoOpt.get();
                solicitud.setAnalista(nuevo);
                solicitud.setFechaAsignacion(LocalDateTime.now());
                mensaje = "Estado cambiado a FIRMA RECIBIDA. Reasignado a " + nuevo.getNombre() + " porque " + analistaActual.getNombre() + " no está de turno.";
            } else {
                mensaje = "Estado cambiado a FIRMA RECIBIDA. No hay analistas en turno disponibles; se mantiene asignado a " + analistaActual.getNombre() + ".";
            }
        }

        Aliado aliado = solicitud.getAliado();

        Analista analistaAsignado = solicitud.getAnalista();
        String reasignacionPrefix = "";
        if (analistaActual != null && analistaAsignado != null && !analistaAsignado.getId().equals(analistaActual.getId())) {
            reasignacionPrefix = "Solicitud reasignada\n\n";
        }

        String telegramMensaje = String.format(
                reasignacionPrefix + "Firma digital recibida\nCliente: %s\nEmpresa: %s\nAliado: %s\nAnalista: %s\nEstado: Firma recibida",
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
                aliado.getNombre(),
                analistaAsignado != null ? analistaAsignado.getNombre() : "No asignado"
        );

        String respuesta = notificationPort.sendMessage(telegramProperties.getSalesChatId(), telegramMensaje);

        HistorialNotificacion historial = HistorialNotificacion.builder()
                .solicitud(solicitud)
                .canal(Canal.TELEGRAM)
                .origen(Origen.VENDEDOR)
                .destino(Destino.GRUPO_ANALISTAS)
                .mensajeEnviado(telegramMensaje)
                .estadoEnvio(respuesta.contains("error") || respuesta.startsWith("Error:") ? EstadoEnvio.ERROR : EstadoEnvio.ENVIADO)
                .respuestaIntegracion(respuesta)
                .build();

        historialRepository.save(historial);

        solicitud.setEstado(EstadoSolicitud.FIRMA_RECIBIDA);
        solicitud = solicitudRepository.save(solicitud);

        return mapToResponse(solicitud, mensaje);
    }

    public SolicitudResponse marcarRevisado(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.NOTIFICADA) {
            throw new IllegalArgumentException("La solicitud debe estar en estado NOTIFICADA para marcar como revisada");
        }

        String mensajeReasignacion = null;
        Analista analistaActual = solicitud.getAnalista();
        if (analistaActual != null && !asignacionService.estaEnTurno(analistaActual.getId())) {
            java.util.Optional<Analista> nuevoOpt = asignacionService.buscarAnalistaEnTurnoExcluyendo(analistaActual.getId());
            if (nuevoOpt.isPresent()) {
                Analista nuevo = nuevoOpt.get();
                solicitud.setAnalista(nuevo);
                solicitud.setFechaAsignacion(LocalDateTime.now());
                mensajeReasignacion = "Estado cambiado a EN PROCESO. Reasignado a " + nuevo.getNombre() + " porque " + analistaActual.getNombre() + " no está de turno.";
            } else {
                mensajeReasignacion = "Estado cambiado a EN PROCESO. No hay analistas en turno disponibles; se mantiene asignado a " + analistaActual.getNombre() + ".";
            }
        }

        Aliado aliado = solicitud.getAliado();

        Analista analistaAsignado = solicitud.getAnalista();
        String reasignacionPrefix = "";
        if (analistaActual != null && analistaAsignado != null && !analistaAsignado.getId().equals(analistaActual.getId())) {
            reasignacionPrefix = "Solicitud reasignada\n\n";
        }

        String telegramMensaje = String.format(
                reasignacionPrefix + "El vendedor ha revisado la observación\nCliente: %s\nEmpresa: %s\nAliado: %s\nAnalista: %s\nEstado: En proceso",
                solicitud.getCedulaCliente(),
                solicitud.getEmpresa().getNombre(),
                aliado.getNombre(),
                analistaAsignado != null ? analistaAsignado.getNombre() : "No asignado"
        );

        String respuesta = notificationPort.sendMessage(telegramProperties.getSalesChatId(), telegramMensaje);

        HistorialNotificacion historial = HistorialNotificacion.builder()
                .solicitud(solicitud)
                .canal(Canal.TELEGRAM)
                .origen(Origen.VENDEDOR)
                .destino(Destino.GRUPO_ANALISTAS)
                .mensajeEnviado(telegramMensaje)
                .estadoEnvio(respuesta.contains("error") || respuesta.startsWith("Error:") ? EstadoEnvio.ERROR : EstadoEnvio.ENVIADO)
                .respuestaIntegracion(respuesta)
                .build();

        historialRepository.save(historial);

        solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitud = solicitudRepository.save(solicitud);

        return mapToResponse(solicitud, mensajeReasignacion);
    }

    @Transactional(readOnly = true)
    public SolicitudResponse obtenerSolicitud(UUID id) {
        return solicitudRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
    }

    public void eliminarSolicitud(UUID id) {
        solicitudRepository.deleteById(id);
    }

    private SolicitudResponse mapToResponse(Solicitud s) {
        return mapToResponse(s, null);
    }

    private SolicitudResponse mapToResponse(Solicitud s, String mensaje) {
        return SolicitudResponse.builder()
                .id(s.getId())
                .cedulaCliente(s.getCedulaCliente())
                .aliado(SolicitudResponse.AliadoResumen.builder()
                        .id(s.getAliado().getId())
                        .nombre(s.getAliado().getNombre())
                        .build())
                .empresa(SolicitudResponse.EmpresaResumen.builder()
                        .id(s.getEmpresa().getId())
                        .nombre(s.getEmpresa().getNombre())
                        .build())
                .analista(s.getAnalista() != null ? SolicitudResponse.AnalistaResumen.builder()
                        .id(s.getAnalista().getId())
                        .nombre(s.getAnalista().getNombre())
                        .cedula(s.getAnalista().getCedula())
                        .build() : null)
                .estado(s.getEstado())
                .creadoPor(s.getCreadoPor())
                .fechaCreacion(s.getFechaCreacion())
                .fechaAsignacion(s.getFechaAsignacion())
                .fechaFinalizacion(s.getFechaFinalizacion())
                .fechaActualizacion(s.getFechaActualizacion())
                .mensaje(mensaje)
                .build();
    }
}
