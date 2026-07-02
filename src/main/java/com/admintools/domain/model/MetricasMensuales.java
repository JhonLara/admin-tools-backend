package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "metricas_mensuales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricasMensuales {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "periodo", nullable = false, unique = true, length = 7)
    private String periodo;

    @Column(name = "total_solicitudes")
    private int totalSolicitudes;

    @Column(name = "solicitudes_por_estado", columnDefinition = "TEXT")
    private String solicitudesPorEstado;

    @Column(name = "total_notificaciones")
    private int totalNotificaciones;

    @Column(name = "total_sesiones")
    private int totalSesiones;

    @Column(name = "total_empresas")
    private int totalEmpresas;

    @Column(name = "total_aliados")
    private int totalAliados;

    @CreationTimestamp
    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;
}
