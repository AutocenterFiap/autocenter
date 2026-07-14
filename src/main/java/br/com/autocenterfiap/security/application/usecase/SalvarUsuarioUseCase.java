package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.PerfilRepositoryPort;
import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.exception.PerfilNaoEncontradoException;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalvarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public SalvarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                PerfilRepositoryPort perfilRepositoryPort,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.perfilRepositoryPort = perfilRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioJpaEntity executar(UsuarioJpaEntity usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));

        List<PerfilJpaEntity> perfisManaged = usuario.getPerfis().stream()
                .map(perfil -> perfilRepositoryPort.buscarPorNome(perfil.getNome())
                        .orElseThrow(() -> new PerfilNaoEncontradoException("Perfil não encontrado: " + perfil.getId())))
                .toList();

        usuario.setPerfis(perfisManaged);

        return usuarioRepositoryPort.salvar(usuario);
    }
}
