package com.admintools.application.usecase;

import com.admintools.application.dto.SesionActivaResponse;
import com.admintools.application.dto.SesionResumenResponse;
import com.admintools.domain.model.SesionActiva;
import com.admintools.domain.port.SesionActivaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Transactional(readOnly = true)
    public long contarTotalSesiones() {
        return sesionRepository.count();
    }

    @Transactional(readOnly = true)
    public List<SesionResumenResponse> listarResumenPorUsuario() {
        List<SesionActiva> todas = sesionRepository.findAll();

        Map<String, SesionResumenResponse.SesionResumenResponseBuilder> builders = new java.util.HashMap<>();

        for (SesionActiva s : todas) {
            String key = s.getUsername();
            if (!builders.containsKey(key)) {
                builders.put(key, SesionResumenResponse.builder()
                        .username(s.getUsername())
                        .nombre(s.getNombre())
                        .rol(s.getRol())
                        .totalSesiones(0)
                        .sesionesActivas(0));
            }
            builders.get(key).totalSesiones(builders.get(key).build().getTotalSesiones() + 1);
        }

        List<SesionActiva> activas = sesionRepository.findAllByActivaTrueAndFechaExpiracionAfter(LocalDateTime.now());
        for (SesionActiva s : activas) {
            String key = s.getUsername();
            if (builders.containsKey(key)) {
                builders.get(key).sesionesActivas(builders.get(key).build().getSesionesActivas() + 1);
            }
        }

        return builders.values().stream()
                .map(SesionResumenResponse.SesionResumenResponseBuilder::build)
                .collect(Collectors.toList());
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
