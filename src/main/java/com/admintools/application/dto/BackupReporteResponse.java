package com.admintools.application.dto;

import com.admintools.domain.model.TipoBackup;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BackupReporteResponse(
        UUID id,
        TipoBackup tipo,
        String periodo,
        String nombreArchivo,
        int registrosProcesados,
        long tamanoBytes,
        LocalDateTime fechaGeneracion
) {
}
