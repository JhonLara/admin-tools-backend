package com.admintools.application.usecase;

import com.admintools.application.dto.EmpresaRequest;
import com.admintools.application.dto.EmpresaResponse;
import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoEmpresa;
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
public class EmpresaUseCase {

    private final EmpresaRepositoryPort empresaRepository;
    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    public EmpresaResponse crear(EmpresaRequest request) {
        Empresa empresa = Empresa.builder()
                .nombre(request.getNombre())
                .estado(EstadoEmpresa.ACTIVA)
                .build();
        Empresa guardada = empresaRepository.save(empresa);

        String chatId = telegramProperties.getErrorsChatId();
        if (chatId != null && !chatId.isBlank()) {
            String mensaje = String.format(
                    "✅ *NUEVA EMPRESA CREADA*\\n\\n" +
                    "*Nombre:* %s\\n" +
                    "*Creado por:* %s",
                    guardada.getNombre(),
                    "Sistema"
            );
            try {
                notificationPort.sendMessage(chatId, mensaje);
            } catch (Exception e) {
                // No bloquear la creacion si falla la notificacion
            }
        }

        return mapToResponse(guardada);
    }

    public EmpresaResponse actualizar(UUID id, EmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        empresa.setNombre(request.getNombre());
        return mapToResponse(empresaRepository.save(empresa));
    }

    public EmpresaResponse cambiarEstado(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        empresa.setEstado(empresa.getEstado() == EstadoEmpresa.ACTIVA ? EstadoEmpresa.INACTIVA : EstadoEmpresa.ACTIVA);
        return mapToResponse(empresaRepository.save(empresa));
    }

    public void eliminar(UUID id) {
        empresaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
        return empresaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtener(UUID id) {
        return empresaRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
    }

    private EmpresaResponse mapToResponse(Empresa e) {
        return EmpresaResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .estado(e.getEstado())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }
}
