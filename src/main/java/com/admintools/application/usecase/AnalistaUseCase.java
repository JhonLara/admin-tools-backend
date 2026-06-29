package com.admintools.application.usecase;

import com.admintools.application.dto.AnalistaRequest;
import com.admintools.application.dto.AnalistaResponse;
import com.admintools.domain.model.Analista;
import com.admintools.domain.model.EstadoAnalista;
import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.port.AnalistaRepositoryPort;
import com.admintools.domain.port.SolicitudRepositoryPort;
import lombok.RequiredArgsConstructor;
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

    public AnalistaResponse crear(AnalistaRequest request) {
        if (analistaRepository.findByCedula(request.getCedula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un analista con esa cédula");
        }
        Analista analista = Analista.builder()
                .nombre(request.getNombre())
                .cedula(request.getCedula())
                .ordenAsignacion(request.getOrdenAsignacion())
                .estado(EstadoAnalista.ACTIVO)
                .build();
        return mapToResponse(analistaRepository.save(analista));
    }

    public AnalistaResponse actualizar(UUID id, AnalistaRequest request) {
        Analista analista = analistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
        analista.setNombre(request.getNombre());
        analista.setOrdenAsignacion(request.getOrdenAsignacion());
        return mapToResponse(analistaRepository.save(analista));
    }

    public AnalistaResponse cambiarEstado(UUID id) {
        Analista analista = analistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
        analista.setEstado(analista.getEstado() == EstadoAnalista.ACTIVO ? EstadoAnalista.INACTIVO : EstadoAnalista.ACTIVO);
        return mapToResponse(analistaRepository.save(analista));
    }

    @Transactional(readOnly = true)
    public List<AnalistaResponse> listar() {
        return analistaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnalistaResponse> listarActivos() {
        return analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO).stream()
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
                .fechaCreacion(a.getFechaCreacion())
                .fechaActualizacion(a.getFechaActualizacion())
                .build();
    }
}
