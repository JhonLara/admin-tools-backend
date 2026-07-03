package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.BackupReporte;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupReporteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BackupReporteRepositoryAdapter implements BackupReporteRepositoryPort {

    private final JpaBackupReporteRepository jpaRepository;

    @Override
    public BackupReporte save(BackupReporte reporte) {
        return jpaRepository.save(reporte);
    }

    @Override
    public List<BackupReporte> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<BackupReporte> findByTipo(TipoBackup tipo) {
        return jpaRepository.findByTipo(tipo);
    }

    @Override
    public List<BackupReporte> findByPeriodo(String periodo) {
        return jpaRepository.findByPeriodo(periodo);
    }

    @Override
    public Optional<BackupReporte> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<BackupReporte> findByTipoAndPeriodo(TipoBackup tipo, String periodo) {
        return jpaRepository.findByTipoAndPeriodo(tipo, periodo);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
