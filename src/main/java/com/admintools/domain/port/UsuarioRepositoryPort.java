package com.admintools.domain.port;

import com.admintools.domain.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findAll();
    void deleteById(UUID id);
}
