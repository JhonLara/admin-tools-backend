package com.admintools.application.dto;

import com.admintools.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private String id;
    private String token;
    private String username;
    private String nombre;
    private Rol rol;
    private UUID analistaId;
}
