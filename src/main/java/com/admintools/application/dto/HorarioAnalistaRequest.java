package com.admintools.application.dto;

import com.admintools.domain.model.DiaSemana;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class HorarioAnalistaRequest {
    private UUID analistaId;
    private List<DiaSemana> diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
