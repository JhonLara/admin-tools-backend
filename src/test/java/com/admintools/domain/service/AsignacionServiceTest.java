package com.admintools.domain.service;

import com.admintools.domain.model.*;
import com.admintools.domain.port.AnalistaRepositoryPort;
import com.admintools.domain.port.SolicitudRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private AnalistaRepositoryPort analistaRepository;

    @Mock
    private SolicitudRepositoryPort solicitudRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Analista analista1;
    private Analista analista2;
    private Analista analista3;

    @BeforeEach
    void setUp() {
        analista1 = Analista.builder()
                .id(UUID.randomUUID())
                .nombre("Analista 1")
                .cedula("111")
                .ordenAsignacion(1)
                .estado(EstadoAnalista.ACTIVO)
                .build();
        analista2 = Analista.builder()
                .id(UUID.randomUUID())
                .nombre("Analista 2")
                .cedula("222")
                .ordenAsignacion(2)
                .estado(EstadoAnalista.ACTIVO)
                .build();
        analista3 = Analista.builder()
                .id(UUID.randomUUID())
                .nombre("Analista 3")
                .cedula("333")
                .ordenAsignacion(3)
                .estado(EstadoAnalista.ACTIVO)
                .build();
    }

    @Test
    void asignarAnalistaDisponible_debeAsignarPrimerAnalistaCuandoNoHayHistorial() {
        when(analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO))
                .thenReturn(List.of(analista1, analista2, analista3));
        when(solicitudRepository.findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc())
                .thenReturn(Optional.empty());

        Optional<Analista> result = asignacionService.asignarAnalistaDisponible();

        assertTrue(result.isPresent());
        assertEquals(analista1.getId(), result.get().getId());
    }

    @Test
    void asignarAnalistaDisponible_debeAsignarSiguienteEnOrden() {
        Solicitud ultima = Solicitud.builder()
                .analista(analista1)
                .fechaAsignacion(LocalDateTime.now())
                .build();

        when(analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO))
                .thenReturn(List.of(analista1, analista2, analista3));
        when(solicitudRepository.findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc())
                .thenReturn(Optional.of(ultima));

        Optional<Analista> result = asignacionService.asignarAnalistaDisponible();

        assertTrue(result.isPresent());
        assertEquals(analista2.getId(), result.get().getId());
    }

    @Test
    void asignarAnalistaDisponible_debeVolverAlPrimeroCuandoSeAcaban() {
        Solicitud ultima = Solicitud.builder()
                .analista(analista3)
                .fechaAsignacion(LocalDateTime.now())
                .build();

        when(analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO))
                .thenReturn(List.of(analista1, analista2, analista3));
        when(solicitudRepository.findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc())
                .thenReturn(Optional.of(ultima));

        Optional<Analista> result = asignacionService.asignarAnalistaDisponible();

        assertTrue(result.isPresent());
        assertEquals(analista1.getId(), result.get().getId());
    }

    @Test
    void asignarSolicitud_debeLanzarExcepcionCuandoNoHayAnalistas() {
        when(analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO))
                .thenReturn(List.of());

        Solicitud solicitud = Solicitud.builder().cedulaCliente("123").build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> asignacionService.asignarSolicitud(solicitud));

        assertEquals("No hay analistas disponibles para asignar la solicitud", exception.getMessage());
    }
}
