package com.admintools.application.dto;

import com.admintools.domain.model.EstadoUsuario;
import com.admintools.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UsuarioResponse {
    private UUID id;
    private String username;
    private String nombre;
    private Rol rol;
    private UUID analistaId;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
