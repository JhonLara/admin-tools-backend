package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupEjecucion;
import com.admintools.domain.model.TipoBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaBackupEjecucionRepository extends JpaRepository<BackupEjecucion, UUID> {
    List<BackupEjecucion> findByTipo(TipoBackup tipo);
    List<BackupEjecucion> findTop10ByOrderByFechaEjecucionDesc();
}
