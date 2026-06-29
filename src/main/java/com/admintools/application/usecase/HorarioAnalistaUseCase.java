package com.admintools.application.usecase;

import com.admintools.application.dto.HorarioAnalistaRequest;
import com.admintools.application.dto.HorarioAnalistaResponse;
import com.admintools.domain.model.Analista;
import com.admintools.domain.model.HorarioAnalista;
import com.admintools.domain.port.AnalistaRepositoryPort;
import com.admintools.domain.port.HorarioAnalistaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioAnalistaUseCase {

    private final HorarioAnalistaRepositoryPort horarioRepository;
    private final AnalistaRepositoryPort analistaRepository;

    public List<HorarioAnalistaResponse> crear(HorarioAnalistaRequest request) {
        Analista analista = analistaRepository.findById(request.getAnalistaId())
                .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));

        if (request.getDiasSemana() == null || request.getDiasSemana().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un día de la semana");
        }

        List<HorarioAnalista> creados = request.getDiasSemana().stream().map(dia -> {
            HorarioAnalista horario = HorarioAnalista.builder()
                    .analista(analista)
                    .diaSemana(dia)
                    .horaInicio(request.getHoraInicio())
                    .horaFin(request.getHoraFin())
                    .activo(true)
                    .build();
            return horarioRepository.save(horario);
        }).toList();

        return creados.stream().map(this::mapToResponse).toList();
    }

    public HorarioAnalistaResponse actualizar(UUID id, HorarioAnalistaRequest request) {
        HorarioAnalista horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

        if (request.getAnalistaId() != null) {
            Analista analista = analistaRepository.findById(request.getAnalistaId())
                    .orElseThrow(() -> new IllegalArgumentException("Analista no encontrado"));
            horario.setAnalista(analista);
        }

        if (request.getDiasSemana() != null && !request.getDiasSemana().isEmpty()) {
            horario.setDiaSemana(request.getDiasSemana().get(0));
        }
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());

        return mapToResponse(horarioRepository.save(horario));
    }

    public void eliminar(UUID id) {
        HorarioAnalista horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
        horario.setActivo(false);
        horarioRepository.save(horario);
    }

    @Transactional(readOnly = true)
    public List<HorarioAnalistaResponse> listar() {
        return horarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HorarioAnalistaResponse> listarPorAnalista(UUID analistaId) {
        return horarioRepository.findByAnalistaId(analistaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HorarioAnalistaResponse obtener(UUID id) {
        return horarioRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
    }

    private HorarioAnalistaResponse mapToResponse(HorarioAnalista h) {
        return HorarioAnalistaResponse.builder()
                .id(h.getId())
                .analistaId(h.getAnalista().getId())
                .analistaNombre(h.getAnalista().getNombre())
                .diaSemana(h.getDiaSemana())
                .horaInicio(h.getHoraInicio())
                .horaFin(h.getHoraFin())
                .activo(h.getActivo())
                .fechaCreacion(h.getFechaCreacion())
                .fechaActualizacion(h.getFechaActualizacion())
                .build();
    }
}
