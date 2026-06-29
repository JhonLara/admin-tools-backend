package com.admintools.application.usecase;

import com.admintools.application.dto.DashboardResumen;
import com.admintools.application.dto.SolicitudResponse;
import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.port.SolicitudRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardUseCase {

    private final SolicitudRepositoryPort solicitudRepository;

    public DashboardResumen resumen() {
        long total = solicitudRepository.count();
        long notificadas = solicitudRepository.countByEstado(EstadoSolicitud.NOTIFICADA);
        long errores = solicitudRepository.countByEstado(EstadoSolicitud.ERROR_NOTIFICACION);
        long finalizadas = solicitudRepository.countByEstado(EstadoSolicitud.FINALIZADA);
        long pendientes = total - finalizadas;

        List<com.admintools.domain.model.Solicitud> todas = solicitudRepository.findAll();

        Map<String, Long> porAliado = todas.stream()
                .collect(Collectors.groupingBy(s -> s.getAliado().getNombre(), Collectors.counting()));

        Map<String, Long> porEstado = todas.stream()
                .collect(Collectors.groupingBy(s -> s.getEstado().name(), Collectors.counting()));

        List<SolicitudResponse> ultimas = solicitudRepository.findTop10ByOrderByFechaCreacionDesc().stream()
                .map(s -> SolicitudResponse.builder()
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
                        .build())
                .collect(Collectors.toList());

        return DashboardResumen.builder()
                .totalSolicitudes(total)
                .solicitudesPendientes(pendientes)
                .solicitudesNotificadas(notificadas)
                .solicitudesError(errores)
                .solicitudesPorAliado(porAliado.entrySet().stream()
                        .map(e -> DashboardResumen.SolicitudPorAliado.builder()
                                .aliadoNombre(e.getKey())
                                .cantidad(e.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .solicitudesPorEstado(porEstado.entrySet().stream()
                        .map(e -> DashboardResumen.SolicitudPorEstado.builder()
                                .estado(e.getKey())
                                .cantidad(e.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .ultimasSolicitudes(ultimas)
                .build();
    }
}
