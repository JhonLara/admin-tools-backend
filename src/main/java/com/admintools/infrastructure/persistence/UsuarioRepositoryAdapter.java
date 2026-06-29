package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final JpaUsuarioRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll();
    }
}
