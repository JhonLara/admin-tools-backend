package com.admintools.domain.port;

import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.model.Solicitud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudRepositoryPort {
    Solicitud save(Solicitud solicitud);
    Optional<Solicitud> findById(UUID id);
    List<Solicitud> findAll();
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    List<Solicitud> findByAnalistaId(UUID analistaId);
    List<Solicitud> findByAnalistaIdAndEstadoIn(UUID analistaId, List<EstadoSolicitud> estados);
    List<Solicitud> findByCreadoPor(String creadoPor);
    long countByEstado(EstadoSolicitud estado);
    long count();
    List<Solicitud> findTop10ByOrderByFechaCreacionDesc();
    Optional<Solicitud> findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc();
    void deleteById(UUID id);
}
