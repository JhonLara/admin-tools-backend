package com.admintools.application.usecase;

import com.admintools.application.dto.AliadoRequest;
import com.admintools.application.dto.AliadoResponse;
import com.admintools.domain.model.Aliado;
import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoAliado;
import com.admintools.domain.port.AliadoRepositoryPort;
import com.admintools.domain.port.EmpresaRepositoryPort;
import com.admintools.domain.port.NotificationPort;
import com.admintools.infrastructure.config.TelegramProperties;
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
    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

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
        Aliado guardado = aliadoRepository.save(aliado);

        String chatId = telegramProperties.getErrorsChatId();
        if (chatId != null && !chatId.isBlank()) {
            String mensaje = String.format(
                    "✅ *NUEVO ALIADO CREADO*\\n\\n" +
                    "*Nombre:* %s\\n" +
                    "*Empresa(s):* %s\\n" +
                    "*Creado por:* %s",
                    guardado.getNombre(),
                    guardado.getEmpresas().stream().map(Empresa::getNombre).collect(Collectors.joining(", ")),
                    "Sistema"
            );
            try {
                notificationPort.sendMessage(chatId, mensaje);
            } catch (Exception e) {
                // No bloquear la creacion si falla la notificacion
            }
        }

        return mapToResponse(guardado);
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

    public void eliminar(UUID id) {
        aliadoRepository.deleteById(id);
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
