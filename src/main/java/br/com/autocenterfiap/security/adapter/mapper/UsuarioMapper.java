package br.com.autocenterfiap.security.adapter.mapper;

import br.com.autocenterfiap.security.adapter.in.request.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.request.UsuarioRequest;
import br.com.autocenterfiap.security.adapter.in.response.UsuarioResponse;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {
    UsuarioJpaEntity toUsuario(UsuarioRequest usuarioRequest);

    @Mapping(target = "senha", source = "novaSenha")
    UsuarioJpaEntity toUsuario(AlteracaoSenhaRequest alteracaoSenhaRequest);

    UsuarioResponse toUsuarioResponse(UsuarioJpaEntity usuario);

    PerfilJpaEntity toPerfil(PerfilRequest perfilRequest);
    List<PerfilJpaEntity> toPerfis(List<PerfilRequest> perfisRequest);
}
