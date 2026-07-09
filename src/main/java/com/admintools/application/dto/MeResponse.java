package com.admintools.application.dto;

import com.admintools.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MeResponse {
    private String id;
    private String username;
    private String nombre;
    private Rol rol;
    private UUID analistaId;
}
