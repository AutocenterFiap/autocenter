package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoUpdateDTO;
import br.com.autocenterfiap.ordemservico.service.OrdemServicoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ordem-servicos")
@Tag(name = "Ordens de Serviço", description = "API para gerenciamento de ordens de serviço da oficina")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
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
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(ordemServicoService.listarTodos(pageable));
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
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
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
        return ResponseEntity.ok(ordemServicoService.buscarPorNumeroOrdemServico(numeroOs));
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
        OrdemServicoResponseDTO responseDTO = ordemServicoService.criar(dto);
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
        return ResponseEntity.ok(ordemServicoService.atualizar(id, dto));
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
        ordemServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
