package br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class PerfilJpaRepositoryTest {

    @Autowired
    private PerfilJpaRepository perfilRepository;

    @Test
    void deveEncontrarPerfilPorNome() {
        PerfilJpaEntity perfil = new PerfilJpaEntity();
        perfil.setNome(PerfilType.ADMIN);
        perfilRepository.save(perfil);

        Optional<PerfilJpaEntity> encontrado = perfilRepository.findByNome(PerfilType.ADMIN);

        assertTrue(encontrado.isPresent());
        assertEquals(PerfilType.ADMIN, encontrado.get().getNome());
    }

    @Test
    void deveRetornarVazioQuandoPerfilNaoExiste() {
        Optional<PerfilJpaEntity> resultado = perfilRepository.findByNome(PerfilType.ADMIN);

        assertTrue(resultado.isEmpty());
    }
}
