package com.admintools.application.usecase;

import com.admintools.application.dto.UsuarioRequest;
import com.admintools.application.dto.UsuarioResponse;
import com.admintools.domain.model.EstadoUsuario;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.NotificationPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import com.admintools.infrastructure.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationPort notificationPort;
    private final TelegramProperties telegramProperties;

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .rol(request.getRol())
                .analistaId(request.getAnalistaId())
                .estado(EstadoUsuario.ACTIVO)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        String chatId = telegramProperties.getErrorsChatId();
        if (chatId != null && !chatId.isBlank()) {
            String mensaje = String.format(
                    "✅ *NUEVO USUARIO CREADO*\\n\\n" +
                    "*Usuario:* %s\\n" +
                    "*Nombre:* %s\\n" +
                    "*Rol:* %s\\n" +
                    "*Creado por:* %s",
                    guardado.getUsername(),
                    guardado.getNombre(),
                    guardado.getRol().name(),
                    "Sistema"
            );
            try {
                notificationPort.sendMessage(chatId, mensaje);
            } catch (Exception e) {
                // No bloquear la creación si falla la notificación
            }
        }

        return mapToResponse(guardado);
    }

    public UsuarioResponse actualizar(UUID id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setUsername(request.getUsername());
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol());
        usuario.setAnalistaId(request.getAnalistaId());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse cambiarEstado(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setEstado(usuario.getEstado() == EstadoUsuario.ACTIVO ? EstadoUsuario.INACTIVO : EstadoUsuario.ACTIVO);
        return mapToResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(UUID id) {
        return usuarioRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private UsuarioResponse mapToResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nombre(u.getNombre())
                .rol(u.getRol())
                .analistaId(u.getAnalistaId())
                .estado(u.getEstado())
                .fechaCreacion(u.getFechaCreacion())
                .fechaActualizacion(u.getFechaActualizacion())
                .build();
    }
}
