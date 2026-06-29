package com.admintools.domain.port;

import com.admintools.domain.model.HistorialNotificacion;

import java.util.List;
import java.util.UUID;

public interface HistorialNotificacionRepositoryPort {
    HistorialNotificacion save(HistorialNotificacion historial);
    List<HistorialNotificacion> findAll();
    List<HistorialNotificacion> findBySolicitudId(UUID solicitudId);
}
