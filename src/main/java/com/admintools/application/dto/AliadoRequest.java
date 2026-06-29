package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AliadoRequest {
    @NotBlank
    private String nombre;

    @NotNull
    private UUID empresaId;

    private String telegramChatId;
}
