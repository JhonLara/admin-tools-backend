package com.admintools.application.usecase;

import com.admintools.application.dto.AnalistaRequest;
import com.admintools.application.dto.AnalistaResponse;
import com.admintools.domain.model.Analista;
import com.admintools.domain.model.EstadoAnalista;
import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.model.Rol;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.AnalistaRepositoryPort;
import com.admintools.domain.port.SolicitudRepositoryPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalistaUseCase {

    private final AnalistaRepositoryPort analistaRepository;
    private final SolicitudRepositoryPort solicitudRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    private Usuario getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private boolean isSuperAdmin(Usuario u) {
        return u.getRol() == Rol.SUPER_ADMIN;
    }

    private boolean isAdmin(Usuario u) {
        return u.getRol() == Rol.ADMINISTRADOR;
    }

    public AnalistaResponse crear(AnalistaRequest request) {
        if (analistaRepository.findByCedula(request.getCedula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un analista con esa cédula");
        }

        Usuario current = getCurrentUser();
        UUID administradorId = request.getAdministradorId();
        if (administradorId == null) {
            administradorId = current.getId();
        }
        if (!isSuperAdmin(current) && !current.getId().equals(administradorId)) {
            throw new IllegalArgumentException("No puede asignar analistas a otro administrador");
        }

        Analista analista = Analista.builder()
                .nombre(request.getNombre())
                .cedula(request.getCedula())
                .ordenAsignacion(request.getOrdenAsignacion())
                .administradorId(administradorId)
                .estado(EstadoAnalista.ACTIVO)
                .build();
        return mapToResponse(analistaRepository.save(analista));
    }

    public AnalistaResponse actualizar(UUID id, AnalistaRequest request) {
        Analista analista = analistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(analista.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para editar este analista");
        }

        analista.setNombre(request.getNombre());
        analista.setOrdenAsignacion(request.getOrdenAsignacion());
        if (isSuperAdmin(current) && request.getAdministradorId() != null) {
            analista.setAdministradorId(request.getAdministradorId());
        }
        return mapToResponse(analistaRepository.save(analista));
    }

    public AnalistaResponse cambiarEstado(UUID id) {
        Analista analista = analistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(analista.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para cambiar el estado de este analista");
        }

        analista.setEstado(analista.getEstado() == EstadoAnalista.ACTIVO ? EstadoAnalista.INACTIVO : EstadoAnalista.ACTIVO);
        return mapToResponse(analistaRepository.save(analista));
    }

    public void eliminar(UUID id) {
        Analista analista = analistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(analista.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para eliminar este analista");
        }

        analistaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AnalistaResponse> listar() {
        Usuario current = getCurrentUser();
        List<Analista> analistas;
        if (isSuperAdmin(current)) {
            analistas = analistaRepository.findAll();
        } else if (isAdmin(current)) {
            analistas = analistaRepository.findByAdministradorId(current.getId());
        } else {
            analistas = analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO);
        }
        return analistas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnalistaResponse> listarActivos() {
        Usuario current = getCurrentUser();
        List<Analista> analistas;
        if (isSuperAdmin(current)) {
            analistas = analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO);
        } else if (isAdmin(current)) {
            analistas = analistaRepository.findByAdministradorIdAndEstadoOrderByOrdenAsignacionAsc(current.getId(), EstadoAnalista.ACTIVO);
        } else {
            analistas = analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO);
        }
        return analistas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnalistaResponse obtener(UUID id) {
        return analistaRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
    }

    private AnalistaResponse mapToResponse(Analista a) {
        boolean ocupado = !solicitudRepository.findByAnalistaIdAndEstadoIn(
                a.getId(),
                List.of(EstadoSolicitud.CREADA, EstadoSolicitud.ASIGNADA, EstadoSolicitud.NOTIFICADA, EstadoSolicitud.EN_PROCESO, EstadoSolicitud.ERROR_NOTIFICACION)
        ).isEmpty();

        return AnalistaResponse.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .cedula(a.getCedula())
                .ordenAsignacion(a.getOrdenAsignacion())
                .estado(a.getEstado())
                .disponible(!ocupado)
                .administradorId(a.getAdministradorId())
                .fechaCreacion(a.getFechaCreacion())
                .fechaActualizacion(a.getFechaActualizacion())
                .build();
    }
}
