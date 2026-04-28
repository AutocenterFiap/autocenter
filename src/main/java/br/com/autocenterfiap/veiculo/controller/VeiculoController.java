package br.com.autocenterfiap.veiculo.controller;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.dto.VeiculoResponseDTO;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import br.com.autocenterfiap.veiculo.service.VeiculoService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/veiculos")
@Tag(name = "Veículos", description = "API para gerenciamento de veículos da oficina")
public class VeiculoController {
    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @Operation(
            summary = "Buscar veículo por ID",
            description = "Retorna um veículo específico pelo seu identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo encontrado",
                    content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorId(@Parameter(description = "ID do Veículo a ser buscado", required = true)
                                                          @PathVariable Long id){
        VeiculoResponseDTO veiculo = veiculoService.buscarPorId(id);
        return ResponseEntity.ok(veiculo);
    }

    @Operation(
            summary = "Buscar veículo por Placa",
            description = "Retorna um veículo específico pela sua Placa"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo encontrado",
                    content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            )
    })
    @GetMapping("/placa/{placa}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorPlaca(@Parameter(description = "Placa do Veículo a ser buscado", required = true)
                                                          @PathVariable String placa){
        VeiculoResponseDTO veiculo = veiculoService.buscarPorPlaca(placa);
        return ResponseEntity.ok(veiculo);
    }

    @Operation(
            summary = "Listar todos os veículos",
            description = "Retorna uma lista com todos os veículos cadastrados no sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de veículos retornada com sucesso"
    )
    @GetMapping
    public ResponseEntity<Page<VeiculoResponseDTO>> listarTodos(Pageable pageable){
        Page<VeiculoResponseDTO> veiculos = veiculoService.listarTodos(pageable);
        return ResponseEntity.ok(veiculos);
    }

    @Operation(
            summary = "Criar novo veículo",
            description = "Cadastra um novo veículo no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Veículo criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Veiculo.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Placa já cadastrada"
            )
    })
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> criar(@RequestBody @Valid VeiculoDTO veiculoDTO){
        VeiculoResponseDTO veiculo = veiculoService.criar(veiculoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculo);
    }

    @Operation(
            summary = "Atualizar veículo existente",
            description = "Atualiza os dados de um veículo existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Placa,Chassi ou Renavam já cadastrado para outro veículo"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(@Parameter(description = "ID do veículo a ser atualizado", required = true)
                                                        @PathVariable Long id,
                                                        @Parameter(description = "Novos dados do veículo", required = true)
                                                        @RequestBody @Valid VeiculoDTO veiculoDTO){
        VeiculoResponseDTO veiculo = veiculoService.atualizar(id,veiculoDTO);
        return ResponseEntity.status(HttpStatus.OK).body(veiculo);
    }


    @Operation(
            summary = "Deletar veículo",
            description = "Remove um veículo do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Veículo deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do Veículo a ser deletado", required = true)
                                        @PathVariable Long id){
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
