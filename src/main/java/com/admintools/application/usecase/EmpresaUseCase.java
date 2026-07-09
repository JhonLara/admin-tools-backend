package com.admintools.application.usecase;

import com.admintools.application.dto.EmpresaRequest;
import com.admintools.application.dto.EmpresaResponse;
import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoEmpresa;
import com.admintools.domain.model.Rol;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.EmpresaRepositoryPort;
import com.admintools.domain.port.NotificationPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UsuarioRepositoryPort usuarioRepository;

    private Usuario getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private boolean isSuperAdmin(Usuario u) {
        return u.getRol() == Rol.SUPER_ADMIN;
    }

    private boolean isAdmin(Usuario u) {
        return u.getRol() == Rol.ADMINISTRADOR;
    }

    public EmpresaResponse crear(EmpresaRequest request) {
        Usuario current = getCurrentUser();
        if (!isSuperAdmin(current)) {
            throw new IllegalArgumentException("Solo el super admin puede crear empresas");
        }

        Empresa empresa = Empresa.builder()
                .nombre(request.getNombre())
                .estado(EstadoEmpresa.ACTIVA)
                .administradorId(request.getAdministradorId())
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
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(empresa.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para editar esta empresa");
        }

        empresa.setNombre(request.getNombre());
        if (isSuperAdmin(current)) {
            empresa.setAdministradorId(request.getAdministradorId());
        }
        return mapToResponse(empresaRepository.save(empresa));
    }

    public EmpresaResponse cambiarEstado(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(empresa.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para cambiar el estado de esta empresa");
        }

        empresa.setEstado(empresa.getEstado() == EstadoEmpresa.ACTIVA ? EstadoEmpresa.INACTIVA : EstadoEmpresa.ACTIVA);
        return mapToResponse(empresaRepository.save(empresa));
    }

    public void eliminar(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        Usuario current = getCurrentUser();

        if (!isSuperAdmin(current) && !current.getId().equals(empresa.getAdministradorId())) {
            throw new IllegalArgumentException("No tiene permisos para eliminar esta empresa");
        }

        empresaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
        Usuario current = getCurrentUser();
        List<Empresa> empresas;
        if (isSuperAdmin(current)) {
            empresas = empresaRepository.findAll();
        } else if (isAdmin(current)) {
            empresas = empresaRepository.findByAdministradorId(current.getId());
        } else {
            empresas = empresaRepository.findByEstado(EstadoEmpresa.ACTIVA);
        }
        return empresas.stream()
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
                .administradorId(e.getAdministradorId())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }
}
