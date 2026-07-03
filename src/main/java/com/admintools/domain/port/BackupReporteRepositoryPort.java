package com.admintools.domain.port;

import com.admintools.domain.model.BackupReporte;
import com.admintools.domain.model.TipoBackup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupReporteRepositoryPort {
    BackupReporte save(BackupReporte reporte);
    List<BackupReporte> findAll();
    List<BackupReporte> findByTipo(TipoBackup tipo);
    List<BackupReporte> findByPeriodo(String periodo);
    Optional<BackupReporte> findById(UUID id);
    Optional<BackupReporte> findByTipoAndPeriodo(TipoBackup tipo, String periodo);
    void deleteById(UUID id);
}
