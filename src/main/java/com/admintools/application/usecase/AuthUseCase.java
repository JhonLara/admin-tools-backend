package com.admintools.application.usecase;

import com.admintools.application.dto.LoginRequest;
import com.admintools.application.dto.LoginResponse;
import com.admintools.domain.model.EstadoUsuario;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.UsuarioRepositoryPort;
import com.admintools.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepositoryPort usuarioRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol(), usuario.getAnalistaId());

        return LoginResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .analistaId(usuario.getAnalistaId())
                .build();
    }
}
