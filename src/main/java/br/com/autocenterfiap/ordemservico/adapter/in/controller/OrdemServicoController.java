package br.com.autocenterfiap.ordemservico.adapter.in.controller;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoUpdateDTO;
import br.com.autocenterfiap.ordemservico.adapter.mapper.OrdemServicoAdapterMapper;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.AtualizarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ordem-servicos")
@Tag(name = "Ordens de Serviço", description = "API para gerenciamento de ordens de serviço da oficina")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ListarTodasOrdensServicosUseCase listarTodasOrdensServicosUseCase;
    private final BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase;
    private final BuscarOrdemServicoPorNumeroUseCase buscarOrdemServicoPorNumeroUseCase;
    private final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase;
    private final DeletarOrdemServicoUseCase deletarOrdemServicoUseCase;


    public OrdemServicoController(CriarOrdemServicoUseCase criarOrdemServicoUseCase, ListarTodasOrdensServicosUseCase listarTodasOrdensServicosUseCase, BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase, BuscarOrdemServicoPorNumeroUseCase buscarOrdemServicoPorNumeroUseCase, AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase, DeletarOrdemServicoUseCase deletarOrdemServicoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.listarTodasOrdensServicosUseCase = listarTodasOrdensServicosUseCase;
        this.buscarOrdemServicoPorIdUseCase = buscarOrdemServicoPorIdUseCase;
        this.buscarOrdemServicoPorNumeroUseCase = buscarOrdemServicoPorNumeroUseCase;
        this.atualizarOrdemServicoUseCase = atualizarOrdemServicoUseCase;
        this.deletarOrdemServicoUseCase = deletarOrdemServicoUseCase;
    }

    @Operation(
            summary = "Listar todas as ordens de serviço",
            description = "Retorna uma lista paginada com todas as ordens de serviço cadastradas no sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de ordens de serviço retornada com sucesso"
    )
    @GetMapping
    public ResponseEntity<PageResult<OrdemServicoResponseDTO>> listarTodos(Pageable pageable) {

        PaginationRequest pagination = new PaginationRequest(pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(
            listarTodasOrdensServicosUseCase.executar(pagination)
                .map(OrdemServicoAdapterMapper::ordemServicoToOrdemServicoResponseDTO)
        );
    }

    @Operation(
            summary = "Buscar ordem de serviço por ID",
            description = "Retorna uma ordem de serviço específica pelo seu identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ordem de serviço encontrada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem de serviço não encontrada"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@Parameter(description = "ID da Ordem de Serviço a ser buscada", required = true) 
                                                               @PathVariable Long id) {

        OrdemServicoOutput ordemServicoOutput = buscarOrdemServicoPorIdUseCase.executar(id);

        OrdemServicoResponseDTO responseDTO = OrdemServicoAdapterMapper
                .ordemServicoToOrdemServicoResponseDTO(ordemServicoOutput);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Buscar ordem de serviço por número",
            description = "Retorna uma ordem de serviço específica pelo seu número (código gerado)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ordem de serviço encontrada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem de serviço não encontrada"
            )
    })
    @GetMapping("/numero/{numeroOs}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorNumero(@Parameter(description = "Número da Ordem de Serviço a ser buscada", required = true) 
                                                                   @PathVariable Long numeroOs){

        OrdemServicoOutput ordemServicoOutput = buscarOrdemServicoPorNumeroUseCase.executar(numeroOs);

        OrdemServicoResponseDTO ordemServicoResponseDTO = OrdemServicoAdapterMapper
                .ordemServicoToOrdemServicoResponseDTO(ordemServicoOutput);

        return ResponseEntity.ok(ordemServicoResponseDTO);
    }

    @Operation(
            summary = "Criar nova ordem de serviço",
            description = "Cadastra uma nova ordem de serviço associando um veículo e um cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ordem de serviço criada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente ou Veículo não encontrado"
            )
    })
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(@Parameter(description = "Dados para criação da ordem de serviço", required = true)
                                                         @RequestBody @Valid OrdemServicoDTO dto) {

        CriarOrdemServicoInput servicoInput = OrdemServicoAdapterMapper.ordemServicoToCriarOrdemServicoInput(dto);
        OrdemServicoOutput output = criarOrdemServicoUseCase.executar(servicoInput);

        OrdemServicoResponseDTO responseDTO = OrdemServicoAdapterMapper.ordemServicoToOrdemServicoResponseDTO(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(
            summary = "Atualizar status da ordem de serviço",
            description = "Atualiza o status de uma ordem de serviço existente (ex: ABERTA para EM_DIAGNOSTICO)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ordem de serviço atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Transição de status inválida ou dados incorretos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem de serviço não encontrada"
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(@Parameter(description = "ID da Ordem de Serviço", required = true) 
                                                             @PathVariable Long id, 
                                                             @Parameter(description = "Novo status da ordem de serviço", required = true)
                                                             @RequestBody @Valid OrdemServicoUpdateDTO dto) {

        AtualizarOrdemServicoInput atualizarOrdemServicoInput = new AtualizarOrdemServicoInput(dto.statusOS());

        OrdemServicoOutput output = atualizarOrdemServicoUseCase.executar(id, atualizarOrdemServicoInput);

        OrdemServicoResponseDTO responseDTO = OrdemServicoAdapterMapper.ordemServicoToOrdemServicoResponseDTO(output);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Deletar ordem de serviço",
            description = "Remove uma ordem de serviço do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ordem de serviço deletada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem de serviço não encontrada"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID da Ordem de Serviço a ser deletada", required = true) 
                                        @PathVariable Long id) {
        this.deletarOrdemServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
