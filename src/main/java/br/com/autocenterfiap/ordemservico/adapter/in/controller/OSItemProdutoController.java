package br.com.autocenterfiap.ordemservico.adapter.in.controller;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemProdutoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.AdicionarProdutoNaOrdemServicoUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.AtualizarQuantidadeUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.ListarTodosPorOrdemServicoUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.RemoverProdutoNaOrdemServicoUseCase;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ordem-servicos/{osId}/produtos")
@Tag(name = "Produtos na OS", description = "API para gerenciar produtos vinculados a uma Ordem de Serviço")
public class OSItemProdutoController {

    private final AdicionarProdutoNaOrdemServicoUseCase adicionarProdutoNaOrdemServicoUseCase;
    private final AtualizarQuantidadeUseCase atualizarQuantidadeUseCase;
    private final ListarTodosPorOrdemServicoUseCase listarTodosPorOrdemServicoUseCase;
    private final RemoverProdutoNaOrdemServicoUseCase removerProdutoNaOrdemServicoUseCase;

    public OSItemProdutoController(
            AdicionarProdutoNaOrdemServicoUseCase adicionarProdutoNaOrdemServicoUseCase,
            AtualizarQuantidadeUseCase atualizarQuantidadeUseCase,
            ListarTodosPorOrdemServicoUseCase listarTodosPorOrdemServicoUseCase,
            RemoverProdutoNaOrdemServicoUseCase removerProdutoNaOrdemServicoUseCase
    ) {
        this.adicionarProdutoNaOrdemServicoUseCase = adicionarProdutoNaOrdemServicoUseCase;
        this.atualizarQuantidadeUseCase = atualizarQuantidadeUseCase;
        this.listarTodosPorOrdemServicoUseCase = listarTodosPorOrdemServicoUseCase;
        this.removerProdutoNaOrdemServicoUseCase = removerProdutoNaOrdemServicoUseCase;
    }

    @Operation(summary = "Listar produtos da OS", description = "Retorna todos os produtos vinculados a uma Ordem de Serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<Page<OSItemProdutoResponseDTO>> listar(
            @Parameter(description = "ID da Ordem de Serviço", required = true) @PathVariable Long osId,
            Pageable pageable
    ) {

        PaginationRequest pagination = new PaginationRequest(pageable.getPageNumber(), pageable.getPageSize());

        PageResult<OSItemProdutoOutput> result = this.listarTodosPorOrdemServicoUseCase.executar(osId, pagination);

        Page<OSItemProdutoResponseDTO> responseDTOS = new PageImpl<>(
                result.getContent().stream()
                        .map(OSItemProdutoApplicationMapper::outputToOSItemProdutoResponseDTO)
                        .toList(),
                pageable,
                result.getTotalElements()
        );

        return ResponseEntity.ok((responseDTOS));
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
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(dto.produtoId(), dto.quantidade());
        OSItemProdutoOutput output = this.adicionarProdutoNaOrdemServicoUseCase.executar(osId, input);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OSItemProdutoApplicationMapper.outputToOSItemProdutoResponseDTO(output));
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
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(produtoId, dto.quantidade());
        OSItemProdutoOutput output = this.atualizarQuantidadeUseCase.executar(osId, produtoId, input);

        return ResponseEntity.ok(OSItemProdutoApplicationMapper.outputToOSItemProdutoResponseDTO(output));
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
        this.removerProdutoNaOrdemServicoUseCase.executar(osId, produtoId);
        return ResponseEntity.noContent().build();
    }
}