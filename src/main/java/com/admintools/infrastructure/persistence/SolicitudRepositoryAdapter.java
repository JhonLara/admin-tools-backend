package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.model.Solicitud;
import com.admintools.domain.port.SolicitudRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudRepositoryPort {

    private final JpaSolicitudRepository jpaRepository;

    @Override
    public Solicitud save(Solicitud solicitud) {
        return jpaRepository.save(solicitud);
    }

    @Override
    public Optional<Solicitud> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Solicitud> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        return jpaRepository.findByEstado(estado);
    }

    @Override
    public List<Solicitud> findByAnalistaId(UUID analistaId) {
        return jpaRepository.findByAnalistaId(analistaId);
    }

    @Override
    public List<Solicitud> findByAnalistaIdAndEstadoIn(UUID analistaId, List<EstadoSolicitud> estados) {
        return jpaRepository.findByAnalistaIdAndEstadoIn(analistaId, estados);
    }

    @Override
    public long countByEstado(EstadoSolicitud estado) {
        return jpaRepository.countByEstado(estado);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Solicitud> findTop10ByOrderByFechaCreacionDesc() {
        return jpaRepository.findTop10ByOrderByFechaCreacionDesc();
    }

    @Override
    public Optional<Solicitud> findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc() {
        return jpaRepository.findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc();
    }
}
