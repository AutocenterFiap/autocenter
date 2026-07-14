package br.com.autocenterfiap.orcamento.adapter.in;

import br.com.autocenterfiap.cliente.adapter.in.dto.ClienteResponseDTO;
import br.com.autocenterfiap.orcamento.adapter.in.dto.EnvioRequest;
import br.com.autocenterfiap.orcamento.adapter.in.dto.OrcamentoResponse;
import br.com.autocenterfiap.orcamento.adapter.mapper.OrcamentoAdapterMapper;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.dto.PageResult;
import br.com.autocenterfiap.orcamento.application.dto.PaginationRequest;
import br.com.autocenterfiap.orcamento.application.usecase.*;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/orcamentos")
@Tag(name = "Orcamentos", description = "API para gerenciamento de Orcamentos da oficina")
public class OrcamentoController {

    private final BuscarOrcamentoPorIdUseCase buscarOrcamentoPorIdUseCase;
    private final BuscarTodosOrcamentosUseCase buscarTodosOrcamentosUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final ReprovarOrcamentoUseCase reprovarOrcamentoUseCase;

    public OrcamentoController(
            BuscarOrcamentoPorIdUseCase buscarOrcamentoPorIdUseCase,
            BuscarTodosOrcamentosUseCase buscarTosOrcamentosUseCase,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            ReprovarOrcamentoUseCase reprovarOrcamentoUseCase
    ) {
        this.buscarOrcamentoPorIdUseCase = buscarOrcamentoPorIdUseCase;
        this.buscarTodosOrcamentosUseCase = buscarTosOrcamentosUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.reprovarOrcamentoUseCase = reprovarOrcamentoUseCase;
    }

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
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<OrcamentoResponse> aprovar(
            @Parameter(description = "ID do orcamento a ser aprovado", required = true)
            @PathVariable Long id
    ) {
        OrcamentoOutput orcamento = this.aprovarOrcamentoUseCase.executar(id);
        return ResponseEntity.ok(OrcamentoAdapterMapper.orcamentoOutputToOrcamentoResponse(orcamento));
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
    @PatchMapping("/{id}/reprovar")
    public ResponseEntity<OrcamentoResponse> reprovar(
            @Parameter(description = "ID do orcamento a ser reprovado", required = true)
            @PathVariable Long id
    ) {
        OrcamentoOutput orcamento = this.reprovarOrcamentoUseCase.executar(id);
        return ResponseEntity.ok(OrcamentoAdapterMapper.orcamentoOutputToOrcamentoResponse(orcamento));
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

    @Operation(
            summary = "Listar todos os orcamentos",
            description = "Retorna uma lista paginada com todos os orcamentos por status. " +
                    "Por padrão retorna 20 orcamentos por página, ordenados por ID de forma crescente."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de orcamentos retornada com sucesso"
    )
    @GetMapping
    public ResponseEntity<PageResult<OrcamentoResponse>> listarTodos
            (@RequestParam(required = true) StatusOrcamento status, Pageable pageable) {

        PaginationRequest pagination = new PaginationRequest(
                pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(
            this.buscarTodosOrcamentosUseCase.executar(status, pagination)
                .map(OrcamentoAdapterMapper::orcamentoOutputToOrcamentoResponse)
        );
    }

    @Operation(
            summary = "Buscar orcamento por ID",
            description = "Retorna um orcamento específico pelo seu identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "orcamento encontrado",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "orcamento não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponse> buscarPorId(
            @Parameter(description = "ID do orcamento a ser buscado", required = true)
            @PathVariable Long id) {
        OrcamentoOutput orcamento = this.buscarOrcamentoPorIdUseCase.executar(id);

        return ResponseEntity.ok(OrcamentoAdapterMapper.orcamentoOutputToOrcamentoResponse(orcamento));
    }
}
