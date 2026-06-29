package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.HistorialNotificacion;
import com.admintools.domain.port.HistorialNotificacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HistorialNotificacionRepositoryAdapter implements HistorialNotificacionRepositoryPort {

    private final JpaHistorialNotificacionRepository jpaRepository;

    @Override
    public HistorialNotificacion save(HistorialNotificacion historial) {
        return jpaRepository.save(historial);
    }

    @Override
    public List<HistorialNotificacion> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<HistorialNotificacion> findBySolicitudId(UUID solicitudId) {
        return jpaRepository.findBySolicitudId(solicitudId);
    }
}
