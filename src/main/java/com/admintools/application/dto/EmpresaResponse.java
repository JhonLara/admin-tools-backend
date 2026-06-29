package com.admintools.application.dto;

import com.admintools.domain.model.EstadoEmpresa;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmpresaResponse {
    private UUID id;
    private String nombre;
    private EstadoEmpresa estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
