package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "backup_ejecuciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupEjecucion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBackup tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBackup estado;

    @Column(name = "periodo", nullable = false)
    private String periodo;

    @Column(name = "registros_procesados")
    private int registrosProcesados;

    @Column(name = "reporte_generado")
    private boolean reporteGenerado;

    @Column(name = "reporte_url")
    private String reporteUrl;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @CreationTimestamp
    @Column(name = "fecha_ejecucion", nullable = false, updatable = false)
    private LocalDateTime fechaEjecucion;
}
