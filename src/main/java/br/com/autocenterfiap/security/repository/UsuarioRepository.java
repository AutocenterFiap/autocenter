package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepository {

    public Optional<Usuario> findByClientId(String clientId){
        String senhaEncoded = new BCryptPasswordEncoder().encode("admin");
        return Optional.of(new Usuario("admin", senhaEncoded));
   }
}
