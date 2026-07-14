package br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioJpaRepositoryTest {

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private PerfilJpaRepository perfilRepository;

    @Test
    void deveEncontrarUsuarioPorNome() {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setNome("maria");
        usuario.setSenha("senha123");
        usuarioRepository.save(usuario);

        Optional<UsuarioJpaEntity> encontrado = usuarioRepository.findByNome("maria");

        assertTrue(encontrado.isPresent());
        assertEquals("maria", encontrado.get().getNome());
    }

    @Test
    void deveEncontrarUsuarioComPerfis() {
        PerfilJpaEntity perfil = new PerfilJpaEntity();
        perfil.setNome(PerfilType.ADMIN);
        perfilRepository.save(perfil);

        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setNome("carlos");
        usuario.setSenha("senha456");
        usuario.setPerfis(List.of(perfil));
        usuarioRepository.save(usuario);

        Optional<UsuarioJpaEntity> encontrado = usuarioRepository.findByNomeWithPerfis("carlos");

        assertTrue(encontrado.isPresent());
        assertEquals("carlos", encontrado.get().getNome());
        assertFalse(encontrado.get().getPerfis().isEmpty());
        assertEquals(PerfilType.ADMIN, encontrado.get().getPerfis().get(0).getNome());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        Optional<UsuarioJpaEntity> resultado = usuarioRepository.findByNome("naoExiste");
        assertTrue(resultado.isEmpty());
    }
}
