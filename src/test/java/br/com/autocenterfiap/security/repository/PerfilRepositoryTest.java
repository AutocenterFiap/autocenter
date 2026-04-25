package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.enums.PerfilType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class PerfilRepositoryTest {

    @Autowired
    private PerfilRepository perfilRepository;

    @Test
    void deveEncontrarPerfilPorNome() {
        Perfil perfil = new Perfil();
        perfil.setNome(PerfilType.ADMIN);
        perfilRepository.save(perfil);

        Optional<Perfil> encontrado = perfilRepository.findByNome(PerfilType.ADMIN);

        assertTrue(encontrado.isPresent());
        assertEquals(PerfilType.ADMIN, encontrado.get().getNome());
    }

    @Test
    void deveRetornarVazioQuandoPerfilNaoExiste() {
        Optional<Perfil> resultado = perfilRepository.findByNome(PerfilType.ADMIN);

        assertTrue(resultado.isEmpty());
    }
}
