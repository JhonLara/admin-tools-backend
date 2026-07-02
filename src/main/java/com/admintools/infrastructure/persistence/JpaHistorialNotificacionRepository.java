package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.HistorialNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHistorialNotificacionRepository extends JpaRepository<HistorialNotificacion, UUID> {
    List<HistorialNotificacion> findBySolicitudId(UUID solicitudId);
    List<HistorialNotificacion> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    void deleteByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
}
