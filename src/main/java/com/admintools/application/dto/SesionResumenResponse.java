package com.admintools.application.dto;

import com.admintools.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SesionResumenResponse {
    private String username;
    private String nombre;
    private Rol rol;
    private long totalSesiones;
    private long sesionesActivas;
}
