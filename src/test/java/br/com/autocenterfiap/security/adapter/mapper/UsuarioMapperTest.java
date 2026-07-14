package br.com.autocenterfiap.security.adapter.mapper;

import br.com.autocenterfiap.security.adapter.in.request.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.request.UsuarioRequest;
import br.com.autocenterfiap.security.adapter.in.response.UsuarioResponse;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisRequestMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioMapperTest {

    private UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void deveMapearUsuarioRequestParaUsuario() {
        UsuarioRequest request = new UsuarioRequest("maria", "123", createPerfisRequestMock());

        UsuarioJpaEntity usuario = usuarioMapper.toUsuario(request);

        assertEquals("maria", usuario.getNome());
        assertEquals("123", usuario.getSenha());
    }

    @Test
    void deveMapearAlteracaoSenhaRequestParaUsuario() {
        AlteracaoSenhaRequest request = new AlteracaoSenhaRequest("maria", "nova123");

        UsuarioJpaEntity usuario = usuarioMapper.toUsuario(request);

        assertEquals("nova123", usuario.getSenha());
    }

    @Test
    void deveMapearUsuarioParaUsuarioResponse() {
        UsuarioJpaEntity usuario = createUsuarioMock(1L, "carlos", "abc", createPerfisMock(PerfilType.ADMIN));

        UsuarioResponse response = usuarioMapper.toUsuarioResponse(usuario);

        assertEquals("carlos", response.nome());
    }

    @Test
    void deveMapearPerfilRequestParaPerfil() {
        PerfilRequest request = new PerfilRequest(PerfilType.ADMIN);

        PerfilJpaEntity perfil = usuarioMapper.toPerfil(request);

        assertEquals(PerfilType.ADMIN, perfil.getNome());
    }

    @Test
    void deveMapearListaDePerfilRequestParaListaDePerfis() {
        PerfilRequest p1 = new PerfilRequest(PerfilType.ADMIN);

        PerfilRequest p2 = new PerfilRequest(PerfilType.WRITE);

        List<PerfilJpaEntity> perfis = usuarioMapper.toPerfis(List.of(p1, p2));

        assertEquals(2, perfis.size());
        assertEquals(PerfilType.ADMIN, perfis.get(0).getNome());
        assertEquals(PerfilType.WRITE, perfis.get(1).getNome());
    }
}
