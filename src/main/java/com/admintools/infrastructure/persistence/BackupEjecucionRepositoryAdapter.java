package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupEjecucion;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupEjecucionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackupEjecucionRepositoryAdapter implements BackupEjecucionRepositoryPort {

    private final JpaBackupEjecucionRepository jpaRepository;

    @Override
    public BackupEjecucion save(BackupEjecucion ejecucion) {
        return jpaRepository.save(ejecucion);
    }

    @Override
    public List<BackupEjecucion> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<BackupEjecucion> findByTipo(TipoBackup tipo) {
        return jpaRepository.findByTipo(tipo);
    }

    @Override
    public List<BackupEjecucion> findTop10ByOrderByFechaEjecucionDesc() {
        return jpaRepository.findTop10ByOrderByFechaEjecucionDesc();
    }
}
