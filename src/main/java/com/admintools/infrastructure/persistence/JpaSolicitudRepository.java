package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.EstadoSolicitud;
import com.admintools.domain.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaSolicitudRepository extends JpaRepository<Solicitud, UUID> {
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    List<Solicitud> findByAnalistaId(UUID analistaId);
    List<Solicitud> findByAnalistaIdAndEstadoIn(UUID analistaId, List<EstadoSolicitud> estados);
    long countByEstado(EstadoSolicitud estado);
    long count();
    List<Solicitud> findTop10ByOrderByFechaCreacionDesc();
    Optional<Solicitud> findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc();
}
