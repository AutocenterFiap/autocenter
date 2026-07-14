package br.com.autocenterfiap.security.application.port;

import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;

import java.util.Optional;

public interface PerfilRepositoryPort {

    Optional<PerfilJpaEntity> buscarPorNome(PerfilType nome);
}
