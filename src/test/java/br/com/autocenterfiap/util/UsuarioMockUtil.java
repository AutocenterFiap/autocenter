package br.com.autocenterfiap.util;

import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.request.UsuarioRequest;
import br.com.autocenterfiap.security.adapter.in.response.PerfilResponse;
import br.com.autocenterfiap.security.adapter.in.response.UsuarioResponse;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;

import java.util.List;

public final class UsuarioMockUtil {
    private UsuarioMockUtil() {
    }
    public static UsuarioJpaEntity createUsuarioMock(Long id, String nome, String senha, List<PerfilJpaEntity> perfis){
        return new UsuarioJpaEntity(id, nome, senha, perfis);
    }
    public static UsuarioResponse createUsuarioResponseMock(Long id, String nome, List<PerfilResponse> perfis){
        return new UsuarioResponse(id, nome, perfis);
    }
    public static UsuarioRequest createUsuarioRequestMock(String nome, String senha, List<PerfilRequest> perfis){
        return new UsuarioRequest(nome, senha, perfis);
    }
}
