package com.admintools.application.dto;

import com.admintools.domain.model.Rol;
import lombok.Data;

import java.util.UUID;

@Data
public class UsuarioRequest {
    private String username;
    private String password;
    private String nombre;
    private Rol rol;
    private UUID analistaId;
}
