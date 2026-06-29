package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.HistorialNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHistorialNotificacionRepository extends JpaRepository<HistorialNotificacion, UUID> {
    List<HistorialNotificacion> findBySolicitudId(UUID solicitudId);
}
