package com.admintools.application.usecase;

import com.admintools.application.dto.AliadoRequest;
import com.admintools.application.dto.AliadoResponse;
import com.admintools.domain.model.Aliado;
import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoAliado;
import com.admintools.domain.port.AliadoRepositoryPort;
import com.admintools.domain.port.EmpresaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AliadoUseCase {

    private final AliadoRepositoryPort aliadoRepository;
    private final EmpresaRepositoryPort empresaRepository;

    public AliadoResponse crear(AliadoRequest request) {
        List<Empresa> empresas = request.getEmpresaIds().stream()
                .map(id -> empresaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id)))
                .collect(Collectors.toList());

        Aliado aliado = Aliado.builder()
                .nombre(request.getNombre())
                .empresa(empresas.get(0))
                .empresas(empresas)
                .telegramChatId(request.getTelegramChatId())
                .estado(EstadoAliado.ACTIVO)
                .build();
        return mapToResponse(aliadoRepository.save(aliado));
    }

    public AliadoResponse actualizar(UUID id, AliadoRequest request) {
        Aliado aliado = aliadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
        List<Empresa> empresas = request.getEmpresaIds().stream()
                .map(eid -> empresaRepository.findById(eid)
                        .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + eid)))
                .collect(Collectors.toList());

        aliado.setNombre(request.getNombre());
        aliado.setEmpresa(empresas.get(0));
        aliado.setEmpresas(empresas);
        aliado.setTelegramChatId(request.getTelegramChatId());
        return mapToResponse(aliadoRepository.save(aliado));
    }

    public AliadoResponse cambiarEstado(UUID id) {
        Aliado aliado = aliadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
        aliado.setEstado(aliado.getEstado() == EstadoAliado.ACTIVO ? EstadoAliado.INACTIVO : EstadoAliado.ACTIVO);
        return mapToResponse(aliadoRepository.save(aliado));
    }

    @Transactional(readOnly = true)
    public List<AliadoResponse> listar() {
        return aliadoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AliadoResponse> listarActivos() {
        return aliadoRepository.findByEstado(EstadoAliado.ACTIVO).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AliadoResponse obtener(UUID id) {
        return aliadoRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
    }

    private AliadoResponse mapToResponse(Aliado a) {
        return AliadoResponse.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .empresa(AliadoResponse.EmpresaResumen.builder()
                        .id(a.getEmpresa().getId())
                        .nombre(a.getEmpresa().getNombre())
                        .build())
                .empresas(a.getEmpresas().stream()
                        .map(e -> AliadoResponse.EmpresaResumen.builder()
                                .id(e.getId())
                                .nombre(e.getNombre())
                                .build())
                        .collect(Collectors.toList()))
                .telegramChatId(a.getTelegramChatId())
                .estado(a.getEstado())
                .fechaCreacion(a.getFechaCreacion())
                .fechaActualizacion(a.getFechaActualizacion())
                .build();
    }
}
