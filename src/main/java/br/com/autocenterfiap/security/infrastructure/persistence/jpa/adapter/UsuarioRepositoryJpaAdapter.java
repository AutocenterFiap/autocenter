package br.com.autocenterfiap.security.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository.UsuarioJpaRepository;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryJpaAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioRepository;

    public UsuarioRepositoryJpaAdapter(UsuarioJpaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<UsuarioJpaEntity> buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }

    @Override
    public Optional<UsuarioJpaEntity> buscarPorNomeComPerfis(String nome) {
        return usuarioRepository.findByNomeWithPerfis(nome);
    }

    @Override
    public UsuarioJpaEntity salvar(UsuarioJpaEntity usuario) {
        return usuarioRepository.save(usuario);
    }
}
