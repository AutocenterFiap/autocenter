package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.model.Perfil;
import br.com.autocenterfiap.security.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    public Optional<Usuario> findByUsuario(String usuario){
        Usuario usuarioMock = getUsuarioMock(usuario);
        return usuarioMock == null ? Optional.empty(): Optional.of(usuarioMock);
   }

   private Usuario getUsuarioMock(String usuario){
       String SENHA_MOCK = "admin";
       String USUARIO_MOCK = "admin";
       if (usuario.equals(USUARIO_MOCK)){
           String senhaEncoded = new BCryptPasswordEncoder().encode(SENHA_MOCK);
           return new Usuario(usuario, senhaEncoded, List.of(new Perfil(PerfilType.ADMIN)));
       }
       return null;
   }
}
