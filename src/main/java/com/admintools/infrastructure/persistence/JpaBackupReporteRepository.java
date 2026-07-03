package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupReporte;
import com.admintools.domain.model.TipoBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBackupReporteRepository extends JpaRepository<BackupReporte, UUID> {
    List<BackupReporte> findByTipo(TipoBackup tipo);
    List<BackupReporte> findByPeriodo(String periodo);
    Optional<BackupReporte> findByTipoAndPeriodo(TipoBackup tipo, String periodo);
}
