package br.com.autocenterfiap.orcamento.controller;

import br.com.autocenterfiap.cliente.dto.ClienteDTO;
import br.com.autocenterfiap.cliente.dto.ClienteResponseDTO;
import br.com.autocenterfiap.orcamento.dto.EnvioRequest;
import br.com.autocenterfiap.orcamento.repository.OrcamentoRepository;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.orcamento.service.OrcamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PatchExchange;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orcamentos")
@Tag(name = "Orcamentos", description = "API para gerenciamento de Orcamentos da oficina")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @Operation(
            summary = "Aprovar orcamento",
            description = "Atualiza o status do orcamento para aprovado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orcamento aprovado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orcamento não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            )
    })
    @PatchExchange("/{id}/aprovar")
    public ResponseEntity<OrcamentoResponse> aprovar(
            @Parameter(description = "ID do orcamento a ser aprovado", required = true)
            @PathVariable Long id
    ) {
        Orcamento orcamento = orcamentoService.aprovar(id);
        return ResponseEntity.ok(Orcamento.paraOrcamentoResponse(orcamento));
    }

    @Operation(
            summary = "Reprovar orcamento",
            description = "Atualiza o status do orcamento para reprovado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orcamento reprovado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orcamento não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            )
    })
    @PatchExchange("/{id}/reprovar")
    public ResponseEntity<OrcamentoResponse> reprovar(
            @Parameter(description = "ID do orcamento a ser reprovado", required = true)
            @PathVariable Long id
    ) {
        Orcamento orcamento = orcamentoService.reprovar(id);
        return ResponseEntity.ok(Orcamento.paraOrcamentoResponse(orcamento));
    }

    @Operation(
            summary = "Enviar orcamento",
            description = "Envia o orcamento para o cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orcamento enviado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orcamento não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            )
    })
    @PostMapping("/{id}/enviar-cliente")
    public ResponseEntity<String> enviarOrcamento(
            @PathVariable Long id,
            @RequestBody EnvioRequest envioRequest) {

        String mensagem = String.format("Orçamento %d enviado via %s com sucesso!",
                id, envioRequest.tipo().name());

        return ResponseEntity.ok(mensagem);
    }
}
