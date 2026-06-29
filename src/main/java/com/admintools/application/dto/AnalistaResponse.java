package com.admintools.application.dto;

import com.admintools.domain.model.EstadoAnalista;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AnalistaResponse {
    private UUID id;
    private String nombre;
    private String cedula;
    private Integer ordenAsignacion;
    private EstadoAnalista estado;
    private Boolean disponible;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
