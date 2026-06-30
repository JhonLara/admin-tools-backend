package com.admintools.application.dto;

import com.admintools.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SesionActivaResponse {
    private UUID id;
    private String username;
    private String nombre;
    private Rol rol;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaExpiracion;
    private Boolean activa;
}
