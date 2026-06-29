package com.admintools.application.dto;

import com.admintools.domain.model.Canal;
import com.admintools.domain.model.Destino;
import com.admintools.domain.model.EstadoEnvio;
import com.admintools.domain.model.Origen;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HistorialNotificacionResponse {
    private UUID id;
    private UUID solicitudId;
    private String cedulaCliente;
    private String nombreAliado;
    private Canal canal;
    private Origen origen;
    private Destino destino;
    private String mensajeEnviado;
    private EstadoEnvio estadoEnvio;
    private String respuestaIntegracion;
    private LocalDateTime fechaEnvio;
}
