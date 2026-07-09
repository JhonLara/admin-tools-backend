package com.admintools.application.usecase;

import com.admintools.application.dto.LoginRequest;
import com.admintools.application.dto.LoginResponse;
import com.admintools.domain.model.EstadoUsuario;
import com.admintools.domain.model.Rol;
import com.admintools.domain.model.SesionActiva;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.SesionActivaRepositoryPort;
import com.admintools.domain.port.UsuarioRepositoryPort;
import com.admintools.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepositoryPort usuarioRepository;
    private final SesionActivaRepositoryPort sesionRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        // Invalidar sesiones previas del mismo usuario, excepto si es VENDEDOR
        if (usuario.getRol() != Rol.VENDEDOR) {
            sesionRepository.invalidateByUsername(usuario.getUsername());
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol(), usuario.getAnalistaId());

        SesionActiva sesion = SesionActiva.builder()
                .token(token)
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtUtil.getExpirationSeconds()))
                .activa(true)
                .build();
        sesionRepository.save(sesion);

        return LoginResponse.builder()
                .id(usuario.getId().toString())
                .token(token)
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .analistaId(usuario.getAnalistaId())
                .build();
    }
}
