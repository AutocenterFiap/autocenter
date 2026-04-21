package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.mapper.UsuarioMapper;
import br.com.autocenterfiap.security.model.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.model.UsuarioRequest;
import br.com.autocenterfiap.security.model.UsuarioResponse;
import br.com.autocenterfiap.security.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{nome}")
    public ResponseEntity<UsuarioResponse> obter(@PathVariable String nome){
        var usuario = usuarioMapper.toUsuarioResponse(usuarioService.findByNome(nome));
        return ResponseEntity.ok(usuario);
    }

    @PostMapping()
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest usuarioRequest,
            UriComponentsBuilder uriBuilder){

        var uri = uriBuilder.path("usuarios/{nomeUsuario}").buildAndExpand(usuarioRequest.nome()).toUri();

        var usuario = usuarioMapper.toUsuario(usuarioRequest);

        var usuarioSalvo = usuarioService.salvar(usuario);

        var usuarioResponse = usuarioMapper.toUsuarioResponse(usuarioSalvo);

        return ResponseEntity.created(uri).body(usuarioResponse);
    }

    @PatchMapping("/senha/alteracao")
    public ResponseEntity<UsuarioResponse> alterar(@Valid @RequestBody AlteracaoSenhaRequest alteracaoSenhaRequest){
        var usuarioDadosAlteracao = usuarioMapper.toUsuario(alteracaoSenhaRequest);

        var usuarioAtualizado = usuarioService.alterarSenha(usuarioDadosAlteracao);

        var usuarioResponse = usuarioMapper.toUsuarioResponse(usuarioAtualizado);

        return ResponseEntity.ok(usuarioResponse);
    }
}
