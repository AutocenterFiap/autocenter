package br.com.autocenterfiap.security.mapper;

import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import br.com.autocenterfiap.security.controller.request.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.controller.request.PerfilRequest;
import br.com.autocenterfiap.security.controller.request.UsuarioRequest;
import br.com.autocenterfiap.security.controller.response.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toUsuario(UsuarioRequest usuarioRequest);

    @Mapping(target = "senha", source = "novaSenha")
    Usuario toUsuario(AlteracaoSenhaRequest alteracaoSenhaRequest);

    UsuarioResponse toUsuarioResponse(Usuario usuario);

    Perfil toPerfil(PerfilRequest perfilRequest);
    List<Perfil> toPerfis(List<PerfilRequest> perfisRequest);
}
