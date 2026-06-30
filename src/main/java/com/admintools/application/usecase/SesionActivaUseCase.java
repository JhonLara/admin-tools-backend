package com.admintools.application.usecase;

import com.admintools.application.dto.SesionActivaResponse;
import com.admintools.domain.model.SesionActiva;
import com.admintools.domain.port.SesionActivaRepositoryPort;
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
public class SesionActivaUseCase {

    private final SesionActivaRepositoryPort sesionRepository;

    @Transactional(readOnly = true)
    public List<SesionActivaResponse> listarActivas() {
        return sesionRepository.findAllByActivaTrueAndFechaExpiracionAfter(LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarActivas() {
        return sesionRepository.countByActivaTrueAndFechaExpiracionAfter(LocalDateTime.now());
    }

    public void invalidarSesion(UUID id) {
        SesionActiva sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        sesion.setActiva(false);
        sesionRepository.save(sesion);
    }

    private SesionActivaResponse mapToResponse(SesionActiva s) {
        return SesionActivaResponse.builder()
                .id(s.getId())
                .username(s.getUsername())
                .nombre(s.getNombre())
                .rol(s.getRol())
                .ipAddress(s.getIpAddress())
                .userAgent(s.getUserAgent())
                .fechaInicio(s.getFechaInicio())
                .fechaExpiracion(s.getFechaExpiracion())
                .activa(s.getActiva())
                .build();
    }
}
