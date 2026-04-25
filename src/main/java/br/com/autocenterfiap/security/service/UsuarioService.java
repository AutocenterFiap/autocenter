package br.com.autocenterfiap.security.service;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.exception.PerfilNaoEncontradoException;
import br.com.autocenterfiap.security.exception.UsuarioNaoEncontradoException;
import br.com.autocenterfiap.security.repository.PerfilRepository;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario salvar(Usuario usuario){
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));

        List<Perfil> perfisManaged = usuario.getPerfis().stream()
                .map(perfil -> perfilRepository.findByNome(perfil.getNome())
                        .orElseThrow(() -> new PerfilNaoEncontradoException("Perfil não encontrado: " + perfil.getId())))
                .toList();

        usuario.setPerfis(perfisManaged);

        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
        return usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Usuario findByNome(String nome) {
        return usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("O usuário não foi encontrado!"));
    }

    public Usuario alterarSenha(Usuario usuario) {
        Usuario usuarioEncontrado = usuarioRepository.findByNome(usuario.getNome())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("O usuário não foi encontrado!"));
        usuarioEncontrado.setSenha(usuario.getSenha());
        return usuarioRepository.save(usuario);
    }

    public Usuario findByNomeWithPerfis(String nome){
        return usuarioRepository.findByNomeWithPerfis(nome)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("O usuário não foi encontrado!"));
    }
}
