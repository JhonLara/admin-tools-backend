package com.admintools.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResumen {
    private long totalSolicitudes;
    private long solicitudesPendientes;
    private long solicitudesNotificadas;
    private long solicitudesError;
    private List<SolicitudPorAliado> solicitudesPorAliado;
    private List<SolicitudPorEstado> solicitudesPorEstado;
    private List<SolicitudResponse> ultimasSolicitudes;

    @Data
    @Builder
    public static class SolicitudPorAliado {
        private String aliadoNombre;
        private long cantidad;
    }

    @Data
    @Builder
    public static class SolicitudPorEstado {
        private String estado;
        private long cantidad;
    }
}
