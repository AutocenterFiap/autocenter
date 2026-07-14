package br.com.autocenterfiap.security.infrastructure.security;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioDetailsService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
        return usuarioRepositoryPort.buscarPorNome(nome)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }
}
