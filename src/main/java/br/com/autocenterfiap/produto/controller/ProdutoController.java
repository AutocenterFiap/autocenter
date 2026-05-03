package br.com.autocenterfiap.produto.controller;

import br.com.autocenterfiap.produto.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.dto.ProdutoResponseDTO;
import br.com.autocenterfiap.produto.service.ProdutoService;
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
@RequestMapping("/v1/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos e Insumos", description = "API para gerenciamento de produtos, insumos e controle de estoque")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(summary = "Listar produtos", description = "Retorna produtos ativos com filtros opcionais por categoria e busca por nome ou código")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(
            @Parameter(description = "Filtrar por categoria (ex: Motor, Freios, Elétrica, Fluidos)")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Busca por nome ou código do produto")
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(produtoService.listar(categoria, busca));
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Criar produto", description = "Cadastra um novo produto ou insumo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Código já cadastrado")
    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(dto));
    }

    @Operation(summary = "Atualizar produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Código já utilizado por outro produto")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto
    ) {
        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    @Operation(summary = "Desativar produto", description = "Realiza soft delete — o produto é marcado como inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id
    ) {
        produtoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // Controle de Estoque

    @Operation(summary = "Entrada de estoque", description = "Registra entrada de quantidade no estoque do produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/estoque/adicionar")
    public ResponseEntity<ProdutoResponseDTO> adicionarEstoque(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueDTO dto
    ) {
        return ResponseEntity.ok(produtoService.adicionarEstoque(id, dto));
    }

    @Operation(summary = "Saída manual de estoque", description = "Registra saída manual de quantidade do estoque do produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    @PatchMapping("/{id}/estoque/remover")
    public ResponseEntity<ProdutoResponseDTO> removerEstoque(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueDTO dto
    ) {
        return ResponseEntity.ok(produtoService.removerEstoque(id, dto));
    }

    @Operation(summary = "Alertas de estoque", description = "Lista produtos com estoque baixo (LOW_STOCK) ou sem estoque (OUT_OF_STOCK)")
    @ApiResponse(responseCode = "200", description = "Lista de alertas retornada com sucesso")
    @GetMapping("/estoque/alertas")
    public ResponseEntity<List<ProdutoResponseDTO>> listarAlertasEstoque() {
        return ResponseEntity.ok(produtoService.listarProdutosComProblemaDeEstoque());
    }
}
