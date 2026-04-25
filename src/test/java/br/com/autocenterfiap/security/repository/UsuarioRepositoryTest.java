package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import br.com.autocenterfiap.security.enums.PerfilType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Test
    void deveEncontrarUsuarioPorNome() {
        Usuario usuario = new Usuario();
        usuario.setNome("maria");
        usuario.setSenha("senha123");
        usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByNome("maria");

        assertTrue(encontrado.isPresent());
        assertEquals("maria", encontrado.get().getNome());
    }

    @Test
    void deveEncontrarUsuarioComPerfis() {
        Perfil perfil = new Perfil();
        perfil.setNome(PerfilType.ADMIN);
        perfilRepository.save(perfil);

        Usuario usuario = new Usuario();
        usuario.setNome("carlos");
        usuario.setSenha("senha456");
        usuario.setPerfis(List.of(perfil));
        usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByNomeWithPerfis("carlos");

        assertTrue(encontrado.isPresent());
        assertEquals("carlos", encontrado.get().getNome());
        assertFalse(encontrado.get().getPerfis().isEmpty());
        assertEquals(PerfilType.ADMIN, encontrado.get().getPerfis().get(0).getNome());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByNome("naoExiste");
        assertTrue(resultado.isEmpty());
    }
}
