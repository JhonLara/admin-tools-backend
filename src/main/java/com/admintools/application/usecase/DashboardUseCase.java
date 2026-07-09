package com.admintools.application.usecase;

import com.admintools.application.dto.DashboardResumen;
import com.admintools.application.dto.SolicitudResponse;
import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.model.Rol;
import com.admintools.domain.model.Solicitud;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.SolicitudRepositoryPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    private Usuario getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private boolean perteneceAUsuario(Solicitud s, Usuario u) {
        if (u.getRol() == Rol.SUPER_ADMIN) return true;
        if (u.getRol() == Rol.ADMINISTRADOR) {
            if (u.getId().equals(s.getEmpresa().getAdministradorId())) return true;
            if (s.getAnalista() != null && u.getId().equals(s.getAnalista().getAdministradorId())) return true;
            return false;
        }
        if (u.getRol() == Rol.VENDEDOR) {
            return u.getUsername().equals(s.getCreadoPor());
        }
        if (u.getRol() == Rol.ANALISTA && u.getAnalistaId() != null && s.getAnalista() != null) {
            return u.getAnalistaId().equals(s.getAnalista().getId());
        }
        return false;
    }

    private long contarPorEstado(List<Solicitud> solicitudes, EstadoSolicitud estado) {
        return solicitudes.stream().filter(s -> s.getEstado() == estado).count();
    }

    public DashboardResumen resumen() {
        Usuario current = getCurrentUser();

        List<Solicitud> todas = solicitudRepository.findAll().stream()
                .filter(s -> perteneceAUsuario(s, current))
                .collect(Collectors.toList());

        long total = todas.size();
        long notificadas = contarPorEstado(todas, EstadoSolicitud.NOTIFICADA);
        long errores = contarPorEstado(todas, EstadoSolicitud.ERROR_NOTIFICACION);
        long finalizadas = contarPorEstado(todas, EstadoSolicitud.FINALIZADA);
        long pendientes = total - finalizadas;

        Map<String, Long> porAliado = todas.stream()
                .collect(Collectors.groupingBy(s -> s.getAliado().getNombre(), Collectors.counting()));

        Map<String, Long> porEstado = todas.stream()
                .collect(Collectors.groupingBy(s -> s.getEstado().name(), Collectors.counting()));

        List<SolicitudResponse> ultimas = todas.stream()
                .sorted(Comparator.comparing(Solicitud::getFechaCreacion).reversed())
                .limit(10)
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
