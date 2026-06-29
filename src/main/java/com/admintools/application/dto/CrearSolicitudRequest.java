package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CrearSolicitudRequest {
    @NotBlank
    private String cedulaCliente;

    @NotNull
    private UUID aliadoId;
}
