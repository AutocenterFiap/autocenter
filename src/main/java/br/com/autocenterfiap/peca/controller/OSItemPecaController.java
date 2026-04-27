package br.com.autocenterfiap.peca.controller;

import br.com.autocenterfiap.peca.dto.OSItemPecaRequestDTO;
import br.com.autocenterfiap.peca.dto.OSItemPecaResponseDTO;
import br.com.autocenterfiap.peca.service.OSItemPecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/ordem-servicos/{osId}/pecas")
@RequiredArgsConstructor
@Tag(name = "Peças na OS", description = "API para gerenciar peças vinculadas a uma Ordem de Serviço")
public class OSItemPecaController {

    private final OSItemPecaService osItemPecaService;

    @Operation(summary = "Listar peças da OS", description = "Retorna todas as peças vinculadas a uma Ordem de Serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<List<OSItemPecaResponseDTO>> listar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId
    ) {
        return ResponseEntity.ok(osItemPecaService.listarPorOS(osId));
    }

    @Operation(summary = "Adicionar peça à OS",
            description = "Vincula uma peça à OS e decrementa o estoque imediatamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Peça adicionada com sucesso",
                    content = @Content(schema = @Schema(implementation = OSItemPecaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada"),
            @ApiResponse(responseCode = "422", description = "Peça inativa ou estoque insuficiente")
    })
    @PostMapping
    public ResponseEntity<OSItemPecaResponseDTO> adicionar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Valid @RequestBody OSItemPecaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(osItemPecaService.adicionarPecaNaOS(osId, dto));
    }

    @Operation(summary = "Atualizar quantidade da peça na OS",
            description = "Ajusta a quantidade de uma peça na OS, corrigindo o estoque proporcionalmente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado na OS"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    @PutMapping("/{pecaId}")
    public ResponseEntity<OSItemPecaResponseDTO> atualizar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Parameter(description = "ID da peça", required = true) @PathVariable Long pecaId,
            @Valid @RequestBody OSItemPecaRequestDTO dto
    ) {
        return ResponseEntity.ok(osItemPecaService.atualizarQuantidade(osId, pecaId, dto));
    }

    @Operation(summary = "Remover peça da OS",
            description = "Remove a peça da OS e devolve a quantidade ao estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Peça removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado na OS")
    })
    @DeleteMapping("/{pecaId}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Parameter(description = "ID da peça", required = true) @PathVariable Long pecaId
    ) {
        osItemPecaService.removerPecaDaOS(osId, pecaId);
        return ResponseEntity.noContent().build();
    }
}
