package br.com.autocenterfiap.security.application.port;

import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<UsuarioJpaEntity> buscarPorNome(String nome);

    Optional<UsuarioJpaEntity> buscarPorNomeComPerfis(String nome);

    UsuarioJpaEntity salvar(UsuarioJpaEntity usuario);
}
