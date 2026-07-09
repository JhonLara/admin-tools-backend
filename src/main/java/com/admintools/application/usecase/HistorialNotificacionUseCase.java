package com.admintools.application.usecase;

import com.admintools.application.dto.HistorialNotificacionResponse;
import com.admintools.domain.model.HistorialNotificacion;
import com.admintools.domain.model.Rol;
import com.admintools.domain.model.Solicitud;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.HistorialNotificacionRepositoryPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistorialNotificacionUseCase {

    private final HistorialNotificacionRepositoryPort historialRepository;
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

    public List<HistorialNotificacionResponse> listar() {
        Usuario current = getCurrentUser();
        return historialRepository.findAll().stream()
                .filter(h -> perteneceAUsuario(h.getSolicitud(), current))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HistorialNotificacionResponse mapToResponse(HistorialNotificacion h) {
        return HistorialNotificacionResponse.builder()
                .id(h.getId())
                .solicitudId(h.getSolicitud().getId())
                .cedulaCliente(h.getSolicitud().getCedulaCliente())
                .nombreAliado(h.getSolicitud().getAliado().getNombre())
                .canal(h.getCanal())
                .origen(h.getOrigen())
                .destino(h.getDestino())
                .mensajeEnviado(h.getMensajeEnviado())
                .estadoEnvio(h.getEstadoEnvio())
                .respuestaIntegracion(h.getRespuestaIntegracion())
                .fechaEnvio(h.getFechaEnvio())
                .build();
    }
}
