package br.com.autocenterfiap.security.adapter.in;

import br.com.autocenterfiap.security.adapter.in.request.LoginRequest;
import br.com.autocenterfiap.security.adapter.in.request.RefreshToken;
import br.com.autocenterfiap.security.adapter.in.response.Token;
import br.com.autocenterfiap.security.application.port.TokenPort;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioPorNomeUseCase;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Autenticação", description = "API para autenticação de usuários")
public class AutenticacaoController {
    private final AuthenticationManager authenticationManager;
    private final TokenPort tokenPort;
    private final BuscarUsuarioPorNomeUseCase buscarUsuarioPorNomeUseCase;

    @Operation(
            summary = "Obter Token JWT",
            description = "Retorna um token JWT para o login cadastrado"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Token JWT gerado com sucesso"
    )
    @PostMapping("/token")
    public ResponseEntity<Token> obterToken(@Valid @RequestBody LoginRequest loginRequest){
        var autenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.nome(), loginRequest.senha());
        var authentication = authenticationManager.authenticate(autenticationToken);

        var usuarioAutenticado = (UsuarioJpaEntity) authentication.getPrincipal();
        String tokenAcesso = tokenPort.gerarToken(usuarioAutenticado.getUsername());
        String refreshToken = tokenPort.gerarRefreshToken(usuarioAutenticado.getUsername());

        return ResponseEntity.ok(new Token(tokenAcesso, refreshToken));
    }

    @Operation(
            summary = "Obter novo Token JWT com base no refresh token",
            description = "Retorna um novo token JWT para o Refresh token"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Token JWT gerado com sucesso"
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<Token> atualizarToken(@Valid @RequestBody RefreshToken refreshToken){
        String nomeUsuario = tokenPort.verificarToken(refreshToken.refreshToken());
        var usuario = buscarUsuarioPorNomeUseCase.executar(nomeUsuario);

        String novoTokenAcesso = tokenPort.gerarToken(usuario.getUsername());
        String novoRefreshToken = tokenPort.gerarRefreshToken(usuario.getUsername());

        return ResponseEntity.ok(new Token(novoTokenAcesso, novoRefreshToken));
    }
}
