package com.admintools.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AliadoRequest {
    @NotBlank
    private String nombre;

    @NotEmpty
    private List<UUID> empresaIds;

    private String telegramChatId;
}
