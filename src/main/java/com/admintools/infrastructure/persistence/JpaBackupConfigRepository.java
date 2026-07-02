package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.TipoBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBackupConfigRepository extends JpaRepository<BackupConfig, UUID> {
    Optional<BackupConfig> findByTipo(TipoBackup tipo);
}
