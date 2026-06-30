package com.admintools.domain.service;

import com.admintools.domain.model.*;
import com.admintools.domain.port.AnalistaRepositoryPort;
import com.admintools.domain.port.HorarioAnalistaRepositoryPort;
import com.admintools.domain.port.SolicitudRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AnalistaRepositoryPort analistaRepository;
    private final SolicitudRepositoryPort solicitudRepository;
    private final HorarioAnalistaRepositoryPort horarioRepository;
    private final ErrorNotificationService errorNotificationService;

    public Optional<Analista> asignarAnalistaDisponible() {
        List<Analista> analistasActivos = analistaRepository.findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista.ACTIVO);
        if (analistasActivos.isEmpty()) {
            return Optional.empty();
        }

        // Filtrar analistas que estén dentro de su horario de trabajo actual
        LocalDateTime ahora = LocalDateTime.now();
        DiaSemana diaActual = mapDayOfWeek(ahora.getDayOfWeek());
        LocalTime horaActual = ahora.toLocalTime();

        List<Analista> analistasEnHorario = analistasActivos.stream()
                .filter(a -> estaEnHorario(a.getId(), diaActual, horaActual))
                .toList();

        if (analistasEnHorario.isEmpty()) {
            return Optional.empty();
        }

        Optional<Solicitud> ultimaAsignada = solicitudRepository.findFirstByAnalistaIsNotNullOrderByFechaAsignacionDesc();

        Analista siguiente;
        if (ultimaAsignada.isPresent() && ultimaAsignada.get().getAnalista() != null) {
            int ultimoOrden = ultimaAsignada.get().getAnalista().getOrdenAsignacion();
            siguiente = analistasEnHorario.stream()
                    .filter(a -> a.getOrdenAsignacion() > ultimoOrden)
                    .findFirst()
                    .orElse(analistasEnHorario.get(0));
        } else {
            siguiente = analistasEnHorario.get(0);
        }

        return Optional.of(siguiente);
    }

    private boolean estaEnHorario(UUID analistaId, DiaSemana dia, LocalTime hora) {
        List<HorarioAnalista> horarios = horarioRepository.findByAnalistaIdAndActivoTrue(analistaId);
        if (horarios.isEmpty()) {
            // Si no tiene horarios configurados, se considera disponible siempre
            return true;
        }
        return horarios.stream()
                .anyMatch(h -> h.getDiaSemana() == dia
                        && !hora.isBefore(h.getHoraInicio())
                        && hora.isBefore(h.getHoraFin()));
    }

    private DiaSemana mapDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    public synchronized Solicitud asignarSolicitud(Solicitud solicitud) {
        Optional<Analista> analistaOpt = asignarAnalistaDisponible();
        if (analistaOpt.isEmpty()) {
            errorNotificationService.notifyInconsistency(
                    "No hay analistas disponibles para asignar la solicitud del cliente " + solicitud.getCedulaCliente()
            );
            throw new IllegalStateException("No hay analistas disponibles para asignar la solicitud");
        }

        Analista analista = analistaOpt.get();
        solicitud.setAnalista(analista);
        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
        solicitud.setFechaAsignacion(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }
}
