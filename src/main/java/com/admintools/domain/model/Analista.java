package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(name = "orden_asignacion", nullable = false)
    private Integer ordenAsignacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAnalista estado;

    @Column(name = "administrador_id")
    private UUID administradorId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
}
