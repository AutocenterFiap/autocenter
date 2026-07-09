package br.com.autocenterfiap.veiculo.adapter.in;

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

import br.com.autocenterfiap.veiculo.adapter.in.dto.VeiculoRequestDTO;
import br.com.autocenterfiap.veiculo.adapter.in.dto.VeiculoResponseDTO;
import br.com.autocenterfiap.veiculo.adapter.mapper.VeiculoAdapterMapper;
import br.com.autocenterfiap.veiculo.application.dto.PaginationRequest;
import br.com.autocenterfiap.veiculo.application.usecase.*;

@RestController
@RequestMapping("/v1/veiculos")
@Tag(name = "Veículos", description = "API para gerenciamento de veículos da oficina")
public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    private final BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;
    private final BuscarVeiculoPorPlacaUseCase buscarVeiculoPorPlacaUseCase;
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;

    public VeiculoController(
        CriarVeiculoUseCase criarVeiculoUseCase,
        BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase,
        BuscarVeiculoPorPlacaUseCase buscarVeiculoPorPlacaUseCase,
        ListarVeiculosUseCase listarVeiculosUseCase,
        AtualizarVeiculoUseCase atualizarVeiculoUseCase,
        DeletarVeiculoUseCase deletarVeiculoUseCase
    ) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.buscarVeiculoPorIdUseCase = buscarVeiculoPorIdUseCase;
        this.buscarVeiculoPorPlacaUseCase = buscarVeiculoPorPlacaUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
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
    public ResponseEntity<VeiculoResponseDTO> buscarPorId(
        @Parameter(description = "ID do Veículo a ser buscado", required = true)
        @PathVariable Long id
    ) {
        var output = buscarVeiculoPorIdUseCase.executar(id);
        var response = VeiculoAdapterMapper.veiculoOutputToVeiculoResponse(output);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<VeiculoResponseDTO> buscarPorPlaca(
        @Parameter(description = "Placa do Veículo a ser buscado", required = true)
        @PathVariable String placa
    ) {
        var output = buscarVeiculoPorPlacaUseCase.executar(placa);
        var response = VeiculoAdapterMapper.veiculoOutputToVeiculoResponse(output);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Page<VeiculoResponseDTO>> listarTodos(Pageable pageable) {
        PaginationRequest pagination = new PaginationRequest(
            pageable.getPageNumber(),
            pageable.getPageSize()
        );

        var pageResultOutput = listarVeiculosUseCase.executar(pagination);

        Page<VeiculoResponseDTO> response = new PageImpl<>(
            pageResultOutput.getContent().stream()
                .map(VeiculoAdapterMapper::veiculoOutputToVeiculoResponse)
                .toList(),
            pageable,
            pageResultOutput.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Criar novo veículo",
        description = "Cadastra um novo veículo no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Veículo criado com sucesso",
            content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
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
    public ResponseEntity<VeiculoResponseDTO> criar(
        @RequestBody @Valid VeiculoRequestDTO request
    ) {
        var input = VeiculoAdapterMapper.criarVeiculoRequestToCriarVeiculoInput(request);
        var output = criarVeiculoUseCase.executar(input);
        var response = VeiculoAdapterMapper.veiculoOutputToVeiculoResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Atualizar veículo existente",
        description = "Atualiza os dados de um veículo existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Veículo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
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
            description = "Placa, Chassi ou Renavam já cadastrado para outro veículo"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(
        @Parameter(description = "ID do veículo a ser atualizado", required = true)
        @PathVariable Long id,
        @Parameter(description = "Novos dados do veículo", required = true)
        @RequestBody @Valid VeiculoRequestDTO request
    ) {
        var input = VeiculoAdapterMapper.atualizarVeiculoRequestToAtualizarVeiculoInput(request);
        var output = atualizarVeiculoUseCase.executar(id, input);
        var response = VeiculoAdapterMapper.veiculoOutputToVeiculoResponse(output);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Void> deletar(
        @Parameter(description = "ID do Veículo a ser deletado", required = true)
        @PathVariable Long id
    ) {
        deletarVeiculoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
