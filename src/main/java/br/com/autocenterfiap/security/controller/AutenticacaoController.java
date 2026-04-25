package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.model.LoginRequest;
import br.com.autocenterfiap.security.model.RefreshToken;
import br.com.autocenterfiap.security.model.Token;
import br.com.autocenterfiap.security.service.TokenService;
import br.com.autocenterfiap.security.service.UsuarioService;
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
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

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

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

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
        String nomeUsuario = tokenService.verificarToken(refreshToken.refreshToken());
        var usuario = usuarioService.findByNome(nomeUsuario);

        String novoTokenAcesso = tokenService.gerarToken(usuario);
        String novoRefreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new Token(novoTokenAcesso, novoRefreshToken));
    }
}
