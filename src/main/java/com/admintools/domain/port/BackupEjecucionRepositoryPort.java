package com.admintools.domain.port;

import com.admintools.domain.model.BackupEjecucion;
import com.admintools.domain.model.TipoBackup;

import java.util.List;

public interface BackupEjecucionRepositoryPort {
    BackupEjecucion save(BackupEjecucion ejecucion);
    List<BackupEjecucion> findAll();
    List<BackupEjecucion> findByTipo(TipoBackup tipo);
    List<BackupEjecucion> findTop10ByOrderByFechaEjecucionDesc();
}
