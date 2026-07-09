package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class EmpresaRequest {
    @NotBlank
    private String nombre;

    private UUID administradorId;
}
