package br.com.autocenterfiap.util;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.model.PerfilRequest;
import br.com.autocenterfiap.security.model.PerfilResponse;
import br.com.autocenterfiap.security.model.UsuarioRequest;
import br.com.autocenterfiap.security.model.UsuarioResponse;

import java.util.List;

public final class UsuarioMockUtil {
    private UsuarioMockUtil() {
    }
    public static Usuario createUsuarioMock(Long id, String nome, String senha, List<Perfil> perfis){
        return new Usuario(id, nome, senha, perfis);
    }
    public static UsuarioResponse createUsuarioResponseMock(Long id, String nome, List<PerfilResponse> perfis){
        return new UsuarioResponse(id, nome, perfis);
    }
    public static UsuarioRequest createUsuarioRequestMock(String nome, String senha, List<PerfilRequest> perfis){
        return new UsuarioRequest(nome, senha, perfis);
    }
}
