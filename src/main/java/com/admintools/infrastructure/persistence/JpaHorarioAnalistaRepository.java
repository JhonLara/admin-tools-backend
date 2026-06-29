package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.HorarioAnalista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHorarioAnalistaRepository extends JpaRepository<HorarioAnalista, UUID> {
    List<HorarioAnalista> findByAnalistaId(UUID analistaId);
    List<HorarioAnalista> findByAnalistaIdAndActivoTrue(UUID analistaId);
}
