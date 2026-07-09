package com.admintools.domain.port;

import com.admintools.domain.model.Analista;
import com.admintools.domain.model.EstadoAnalista;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalistaRepositoryPort {
    Analista save(Analista analista);
    Optional<Analista> findById(UUID id);
    List<Analista> findAll();
    List<Analista> findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista estado);
    List<Analista> findByAdministradorId(UUID administradorId);
    List<Analista> findByAdministradorIdAndEstadoOrderByOrdenAsignacionAsc(UUID administradorId, EstadoAnalista estado);
    Optional<Analista> findByCedula(String cedula);
    void deleteById(UUID id);
}
