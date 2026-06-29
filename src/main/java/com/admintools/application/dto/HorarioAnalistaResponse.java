package com.admintools.application.dto;

import com.admintools.domain.model.DiaSemana;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HorarioAnalistaResponse {
    private UUID id;
    private UUID analistaId;
    private String analistaNombre;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
