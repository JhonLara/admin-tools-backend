package com.admintools.application.dto;

import com.admintools.domain.model.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SolicitudResponse {
    private UUID id;
    private String cedulaCliente;
    private AliadoResumen aliado;
    private EmpresaResumen empresa;
    private AnalistaResumen analista;
    private EstadoSolicitud estado;
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaFinalizacion;
    private LocalDateTime fechaActualizacion;
    private String mensaje;

    @Data
    @Builder
    public static class AliadoResumen {
        private UUID id;
        private String nombre;
    }

    @Data
    @Builder
    public static class EmpresaResumen {
        private UUID id;
        private String nombre;
    }

    @Data
    @Builder
    public static class AnalistaResumen {
        private UUID id;
        private String nombre;
        private String cedula;
    }
}
