package com.admintools.domain.port;

import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.TipoBackup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupConfigRepositoryPort {
    BackupConfig save(BackupConfig config);
    Optional<BackupConfig> findById(UUID id);
    Optional<BackupConfig> findByTipo(TipoBackup tipo);
    List<BackupConfig> findAll();
}
