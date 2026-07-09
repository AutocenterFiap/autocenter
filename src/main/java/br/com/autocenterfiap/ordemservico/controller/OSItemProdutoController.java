package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.service.OSItemProdutoService;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoRequestDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoResponseDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ordem-servicos/{osId}/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos na OS", description = "API para gerenciar produtos vinculados a uma Ordem de Serviço")
public class OSItemProdutoController {

    private final OSItemProdutoService osItemProdutoService;

    @Operation(summary = "Listar produtos da OS", description = "Retorna todos os produtos vinculados a uma Ordem de Serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<List<OSItemProdutoResponseDTO>> listar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId
    ) {
        return ResponseEntity.ok(osItemProdutoService.listarPorOS(osId));
    }

    @Operation(summary = "Adicionar produto à OS",
            description = "Vincula um produto à OS e decrementa o estoque imediatamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = OSItemProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "422", description = "Produto inativo ou estoque insuficiente")
    })
    @PostMapping
    public ResponseEntity<OSItemProdutoResponseDTO> adicionar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Valid @RequestBody OSItemProdutoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(osItemProdutoService.adicionarProdutoNaOS(osId, dto));
    }

    @Operation(summary = "Atualizar quantidade do produto na OS",
            description = "Ajusta a quantidade de um produto na OS, corrigindo o estoque proporcionalmente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado na OS"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    @PutMapping("/{produtoId}")
    public ResponseEntity<OSItemProdutoResponseDTO> atualizar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Parameter(description = "ID do produto", required = true) @PathVariable Long produtoId,
            @Valid @RequestBody OSItemProdutoRequestDTO dto
    ) {
        return ResponseEntity.ok(osItemProdutoService.atualizarQuantidade(osId, produtoId, dto));
    }

    @Operation(summary = "Remover produto da OS",
            description = "Remove o produto da OS e devolve a quantidade ao estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado na OS")
    })
    @DeleteMapping("/{produtoId}")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            @Parameter(description = "ID do produto", required = true) @PathVariable Long produtoId
    ) {
        osItemProdutoService.removerProdutoDaOS(osId, produtoId);
        return ResponseEntity.noContent().build();
    }
}
