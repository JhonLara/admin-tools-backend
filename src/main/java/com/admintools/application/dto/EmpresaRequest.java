package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmpresaRequest {
    @NotBlank
    private String nombre;
}
