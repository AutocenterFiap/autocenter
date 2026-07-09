package br.com.autocenterfiap.produto.adapter.in;

import br.com.autocenterfiap.produto.adapter.in.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.ProdutoResponseDTO;
import br.com.autocenterfiap.produto.adapter.mapper.ProdutoAdapterMapper;
import br.com.autocenterfiap.produto.application.usecase.*;
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

    private final ListarProdutosUseCase listarProdutosUseCase;
    private final BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase;
    private final CriarProdutoUseCase criarProdutoUseCase;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;
    private final DesativarProdutoUseCase desativarProdutoUseCase;
    private final AdicionarEstoqueUseCase adicionarEstoqueUseCase;
    private final RemoverEstoqueUseCase removerEstoqueUseCase;
    private final ListarAlertasEstoqueUseCase listarAlertasEstoqueUseCase;

    @Operation(summary = "Listar produtos", description = "Retorna produtos ativos com filtros opcionais por categoria e busca por nome ou código")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(
            @Parameter(description = "Filtrar por categoria (ex: Motor, Freios, Elétrica, Fluidos)")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Busca por nome ou código do produto")
            @RequestParam(required = false) String busca
    ) {
        var outputs = listarProdutosUseCase.executar(categoria, busca);
        var response = outputs.stream()
                .map(ProdutoAdapterMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
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
        var output = buscarProdutoPorIdUseCase.executar(id);
        return ResponseEntity.ok(ProdutoAdapterMapper.toResponse(output));
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
        var input = ProdutoAdapterMapper.toCriarInput(dto);
        var output = criarProdutoUseCase.executar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoAdapterMapper.toResponse(output));
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
        var input = ProdutoAdapterMapper.toAtualizarInput(dto);
        var output = atualizarProdutoUseCase.executar(id, input);
        return ResponseEntity.ok(ProdutoAdapterMapper.toResponse(output));
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
        desativarProdutoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

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
        var input = ProdutoAdapterMapper.toMovimentacaoInput(dto);
        var output = adicionarEstoqueUseCase.executar(id, input);
        return ResponseEntity.ok(ProdutoAdapterMapper.toResponse(output));
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
        var input = ProdutoAdapterMapper.toMovimentacaoInput(dto);
        var output = removerEstoqueUseCase.executar(id, input);
        return ResponseEntity.ok(ProdutoAdapterMapper.toResponse(output));
    }

    @Operation(summary = "Alertas de estoque", description = "Lista produtos com estoque baixo (LOW_STOCK) ou sem estoque (OUT_OF_STOCK)")
    @ApiResponse(responseCode = "200", description = "Lista de alertas retornada com sucesso")
    @GetMapping("/estoque/alertas")
    public ResponseEntity<List<ProdutoResponseDTO>> listarAlertasEstoque() {
        var outputs = listarAlertasEstoqueUseCase.executar();
        var response = outputs.stream()
                .map(ProdutoAdapterMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
