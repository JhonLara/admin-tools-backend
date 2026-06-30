package com.admintools.domain.port;

import com.admintools.domain.model.Aliado;
import com.admintools.domain.model.EstadoAliado;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AliadoRepositoryPort {
    Aliado save(Aliado aliado);
    Optional<Aliado> findById(UUID id);
    List<Aliado> findAll();
    List<Aliado> findByEstado(EstadoAliado estado);
    List<Aliado> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
}
