package br.com.autocenterfiap.security.mapper;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.model.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.model.PerfilRequest;
import br.com.autocenterfiap.security.model.UsuarioRequest;
import br.com.autocenterfiap.security.model.UsuarioResponse;
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

        Usuario usuario = usuarioMapper.toUsuario(request);

        assertEquals("maria", usuario.getNome());
        assertEquals("123", usuario.getSenha());
    }

    @Test
    void deveMapearAlteracaoSenhaRequestParaUsuario() {
        AlteracaoSenhaRequest request = new AlteracaoSenhaRequest("maria", "nova123");

        Usuario usuario = usuarioMapper.toUsuario(request);

        assertEquals("nova123", usuario.getSenha());
    }

    @Test
    void deveMapearUsuarioParaUsuarioResponse() {
        Usuario usuario = createUsuarioMock(1L, "carlos", "abc", createPerfisMock(PerfilType.ADMIN));

        UsuarioResponse response = usuarioMapper.toUsuarioResponse(usuario);

        assertEquals("carlos", response.nome());
    }

    @Test
    void deveMapearPerfilRequestParaPerfil() {
        PerfilRequest request = new PerfilRequest(PerfilType.ADMIN);

        Perfil perfil = usuarioMapper.toPerfil(request);

        assertEquals(PerfilType.ADMIN, perfil.getNome());
    }

    @Test
    void deveMapearListaDePerfilRequestParaListaDePerfis() {
        PerfilRequest p1 = new PerfilRequest(PerfilType.ADMIN);

        PerfilRequest p2 = new PerfilRequest(PerfilType.WRITE);

        List<Perfil> perfis = usuarioMapper.toPerfis(List.of(p1, p2));

        assertEquals(2, perfis.size());
        assertEquals(PerfilType.ADMIN, perfis.get(0).getNome());
        assertEquals(PerfilType.WRITE, perfis.get(1).getNome());
    }
}
