package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.exception.UsuarioNaoEncontradoException;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Service;

@Service
public class AlterarSenhaUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public AlterarSenhaUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public UsuarioJpaEntity executar(UsuarioJpaEntity usuario) {
        UsuarioJpaEntity usuarioEncontrado = usuarioRepositoryPort.buscarPorNome(usuario.getNome())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("O usuário não foi encontrado!"));
        usuarioEncontrado.setSenha(usuario.getSenha());
        return usuarioRepositoryPort.salvar(usuario);
    }
}
