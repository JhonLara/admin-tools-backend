package com.admintools.domain.port;

import com.admintools.domain.model.HistorialNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HistorialNotificacionRepositoryPort {
    HistorialNotificacion save(HistorialNotificacion historial);
    List<HistorialNotificacion> findAll();
    List<HistorialNotificacion> findBySolicitudId(UUID solicitudId);
    List<HistorialNotificacion> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    void deleteByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
}
