package br.com.autocenterfiap.peca.controller;

import br.com.autocenterfiap.peca.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.dto.PecaResponseDTO;
import br.com.autocenterfiap.peca.service.PecaService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/pecas")
@RequiredArgsConstructor
@Tag(name = "Peças e Insumos", description = "API para gerenciamento de peças, insumos e controle de estoque")
public class PecaController {

    private final PecaService pecaService;

    @Operation(summary = "Listar peças", description = "Retorna peças ativas com filtros opcionais por categoria e busca por nome ou código")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<PecaResponseDTO>> listar(
            @Parameter(description = "Filtrar por categoria (ex: Motor, Freios, Elétrica, Fluidos)")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Busca por nome ou código da peça")
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(pecaService.listar(categoria, busca));
    }

    @Operation(summary = "Buscar peça por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peça encontrada",
                    content = @Content(schema = @Schema(implementation = PecaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(
            @Parameter(description = "ID da peça", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(pecaService.buscarPorId(id));
    }

    @Operation(summary = "Criar peça", description = "Cadastra uma nova peça ou insumo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Peça criada com sucesso",
                    content = @Content(schema = @Schema(implementation = PecaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Código já cadastrado")
    })
    @PostMapping
    public ResponseEntity<PecaResponseDTO> criar(
            @Valid @RequestBody PecaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.criar(dto));
    }

    @Operation(summary = "Atualizar peça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peça atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada"),
            @ApiResponse(responseCode = "409", description = "Código já utilizado por outra peça")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> atualizar(
            @Parameter(description = "ID da peça", required = true) @PathVariable Long id,
            @Valid @RequestBody PecaRequestDTO dto
    ) {
        return ResponseEntity.ok(pecaService.atualizar(id, dto));
    }

    @Operation(summary = "Desativar peça", description = "Realiza soft delete — a peça é marcada como inativa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Peça desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @Parameter(description = "ID da peça", required = true) @PathVariable Long id
    ) {
        pecaService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // Controle de Estoque

    @Operation(summary = "Entrada de estoque", description = "Registra entrada de quantidade no estoque da peça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @PatchMapping("/{id}/estoque/adicionar")
    public ResponseEntity<PecaResponseDTO> adicionarEstoque(
            @Parameter(description = "ID da peça", required = true) @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueDTO dto
    ) {
        return ResponseEntity.ok(pecaService.adicionarEstoque(id, dto));
    }

    @Operation(summary = "Saída manual de estoque", description = "Registra saída manual de quantidade do estoque da peça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    @PatchMapping("/{id}/estoque/remover")
    public ResponseEntity<PecaResponseDTO> removerEstoque(
            @Parameter(description = "ID da peça", required = true) @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueDTO dto
    ) {
        return ResponseEntity.ok(pecaService.removerEstoque(id, dto));
    }

    @Operation(summary = "Alertas de estoque", description = "Lista peças com estoque baixo (LOW_STOCK) ou sem estoque (OUT_OF_STOCK)")
    @ApiResponse(responseCode = "200", description = "Lista de alertas retornada com sucesso")
    @GetMapping("/estoque/alertas")
    public ResponseEntity<List<PecaResponseDTO>> listarAlertasEstoque() {
        return ResponseEntity.ok(pecaService.listarPecasComProblemaDeEstoque());
    }
}
