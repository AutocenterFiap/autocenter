package br.com.autocenterfiap.security.mapper;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.model.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.model.PerfilRequest;
import br.com.autocenterfiap.security.model.UsuarioRequest;
import br.com.autocenterfiap.security.model.UsuarioResponse;
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
