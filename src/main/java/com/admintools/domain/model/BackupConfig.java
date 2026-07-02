package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "backup_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoBackup tipo;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = false;

    @Builder.Default
    @Column(name = "retencion_dias", nullable = false)
    private int retencionDias = 90;

    @Builder.Default
    @Column(name = "generar_reporte", nullable = false)
    private boolean generarReporte = true;

    @Column(name = "destino_reporte")
    private String destinoReporte;

    @Column(name = "ultima_ejecucion")
    private java.time.LocalDateTime ultimaEjecucion;

    @Column(name = "siguiente_ejecucion")
    private java.time.LocalDateTime siguienteEjecucion;
}
