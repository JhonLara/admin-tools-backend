package com.admintools.application.dto;

import com.admintools.domain.model.EstadoAliado;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AliadoResponse {
    private UUID id;
    private String nombre;
    private EmpresaResumen empresa;
    private List<EmpresaResumen> empresas;
    private String telegramChatId;
    private EstadoAliado estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Data
    @Builder
    public static class EmpresaResumen {
        private UUID id;
        private String nombre;
    }
}
