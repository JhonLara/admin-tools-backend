package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnalistaRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String cedula;

    @NotNull
    private Integer ordenAsignacion;
}
