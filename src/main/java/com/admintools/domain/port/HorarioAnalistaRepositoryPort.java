package com.admintools.domain.port;

import com.admintools.domain.model.HorarioAnalista;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorarioAnalistaRepositoryPort {
    HorarioAnalista save(HorarioAnalista horario);
    Optional<HorarioAnalista> findById(UUID id);
    List<HorarioAnalista> findAll();
    List<HorarioAnalista> findByAnalistaId(UUID analistaId);
    List<HorarioAnalista> findByAnalistaIdAndActivoTrue(UUID analistaId);
    void deleteById(UUID id);
}
