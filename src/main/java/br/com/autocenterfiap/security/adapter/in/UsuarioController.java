package br.com.autocenterfiap.security.adapter.in;

import br.com.autocenterfiap.security.adapter.in.request.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.adapter.in.request.UsuarioRequest;
import br.com.autocenterfiap.security.adapter.in.response.UsuarioResponse;
import br.com.autocenterfiap.security.application.usecase.AlterarSenhaUseCase;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioPorNomeUseCase;
import br.com.autocenterfiap.security.application.usecase.SalvarUsuarioUseCase;
import br.com.autocenterfiap.security.adapter.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuarios", description = "API para gerenciamento de usuarios do Sistema")
public class UsuarioController {
    private final UsuarioMapper usuarioMapper;
    private final BuscarUsuarioPorNomeUseCase buscarUsuarioPorNomeUseCase;
    private final SalvarUsuarioUseCase salvarUsuarioUseCase;
    private final AlterarSenhaUseCase alterarSenhaUseCase;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Buscar usuario por NOME",
            description = "Retorna um usuario específico pelo seu nome único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario não encontrado"
            )
    })
    @GetMapping("/{nome}")
    public ResponseEntity<UsuarioResponse> obter(@PathVariable String nome){
        var usuario = usuarioMapper.toUsuarioResponse(buscarUsuarioPorNomeUseCase.executar(nome));
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Criar novo usuario",
            description = "Cadastra um novo usuario no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Nome de usuario já cadastrado"
            )
    })
    @PostMapping()
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest usuarioRequest,
            UriComponentsBuilder uriBuilder){

        var uri = uriBuilder.path("usuarios/{nomeUsuario}").buildAndExpand(usuarioRequest.nome()).toUri();

        var usuario = usuarioMapper.toUsuario(usuarioRequest);

        var usuarioSalvo = salvarUsuarioUseCase.executar(usuario);

        var usuarioResponse = usuarioMapper.toUsuarioResponse(usuarioSalvo);

        return ResponseEntity.created(uri).body(usuarioResponse);
    }

    @Operation(
            summary = "Alteração de senha deousuario",
            description = "Altera a senha do usuario no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Senha alterada com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nome de usuario não cadastrado"
            )
    })
    @PatchMapping("/senha/alteracao")
    public ResponseEntity<UsuarioResponse> alterar(@Valid @RequestBody AlteracaoSenhaRequest alteracaoSenhaRequest){
        var usuarioDadosAlteracao = usuarioMapper.toUsuario(alteracaoSenhaRequest);

        var usuarioAtualizado = alterarSenhaUseCase.executar(usuarioDadosAlteracao);

        var usuarioResponse = usuarioMapper.toUsuarioResponse(usuarioAtualizado);

        return ResponseEntity.ok(usuarioResponse);
    }
}
