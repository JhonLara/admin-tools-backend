package com.admintools.infrastructure.config;

import com.admintools.domain.model.BackupConfig;
import com.admintools.domain.model.TipoBackup;
import com.admintools.domain.port.BackupConfigRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupInitializer implements ApplicationRunner {

    private final BackupConfigRepositoryPort backupConfigRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (TipoBackup tipo : TipoBackup.values()) {
            backupConfigRepository.findByTipo(tipo).orElseGet(() -> {
                BackupConfig config = BackupConfig.builder()
                        .tipo(tipo)
                        .activo(false)
                        .retencionDias(90)
                        .generarReporte(true)
                        .destinoReporte("local")
                        .build();
                BackupConfig saved = backupConfigRepository.save(config);
                log.info("Configuracion de backup inicial creada: {} (id={})", tipo, saved.getId());
                return saved;
            });
        }
        log.info("Inicializacion de configuraciones de backup completada");
    }
}
