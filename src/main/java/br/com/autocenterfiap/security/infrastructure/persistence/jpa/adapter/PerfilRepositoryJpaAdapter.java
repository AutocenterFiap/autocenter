package br.com.autocenterfiap.security.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.security.application.port.PerfilRepositoryPort;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository.PerfilJpaRepository;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PerfilRepositoryJpaAdapter implements PerfilRepositoryPort {

    private final PerfilJpaRepository perfilRepository;

    public PerfilRepositoryJpaAdapter(PerfilJpaRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Override
    public Optional<PerfilJpaEntity> buscarPorNome(PerfilType nome) {
        return perfilRepository.findByNome(nome);
    }
}
