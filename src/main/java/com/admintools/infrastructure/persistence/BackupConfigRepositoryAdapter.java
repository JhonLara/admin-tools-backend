package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupConfigRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BackupConfigRepositoryAdapter implements BackupConfigRepositoryPort {

    private final JpaBackupConfigRepository jpaRepository;

    @Override
    public BackupConfig save(BackupConfig config) {
        return jpaRepository.save(config);
    }

    @Override
    public Optional<BackupConfig> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<BackupConfig> findByTipo(TipoBackup tipo) {
        return jpaRepository.findByTipo(tipo);
    }

    @Override
    public List<BackupConfig> findAll() {
        return jpaRepository.findAll();
    }
}
