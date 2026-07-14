package br.com.autocenterfiap.servico.adapter.in;

import br.com.autocenterfiap.servico.adapter.in.dto.ServicoRequestDTO;
import br.com.autocenterfiap.servico.adapter.in.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.adapter.mapper.ServicoAdapterMapper;
import br.com.autocenterfiap.servico.application.dto.PageResult;
import br.com.autocenterfiap.servico.application.dto.PaginationRequest;
import br.com.autocenterfiap.servico.application.usecase.*;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/servicos")
@Tag(name = "Serviços", description = "API para gerenciamento de serviços da oficina")
public class ServicoController {

    private final CriarServicoUseCase criarServicoUseCase;
    private final BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;
    private final ListarServicosUseCase listarServicosUseCase;
    private final ListarServicosPorStatusUseCase listarServicosPorStatusUseCase;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final DeletarServicoUseCase deletarServicoUseCase;

    public ServicoController(
            CriarServicoUseCase criarServicoUseCase,
            BuscarServicoPorIdUseCase buscarServicoPorIdUseCase,
            ListarServicosUseCase listarServicosUseCase,
            ListarServicosPorStatusUseCase listarServicosPorStatusUseCase,
            AtualizarServicoUseCase atualizarServicoUseCase,
            DeletarServicoUseCase deletarServicoUseCase
    ) {
        this.criarServicoUseCase = criarServicoUseCase;
        this.buscarServicoPorIdUseCase = buscarServicoPorIdUseCase;
        this.listarServicosUseCase = listarServicosUseCase;
        this.listarServicosPorStatusUseCase = listarServicosPorStatusUseCase;
        this.atualizarServicoUseCase = atualizarServicoUseCase;
        this.deletarServicoUseCase = deletarServicoUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar um novo serviço", description = "Cria um novo serviço na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoRequestDTO request) {
        var input = ServicoAdapterMapper.criarServicoRequestToCriarServicoInput(request);
        var output = criarServicoUseCase.executar(input);
        var response = ServicoAdapterMapper.servicoOutputToServicoResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso")
    })
    public ResponseEntity<PageResult<ServicoResponseDTO>> listarTodos(Pageable pageable) {
        PaginationRequest pagination = new PaginationRequest(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        return ResponseEntity.ok(
            listarServicosUseCase.executar(pagination)
                .map(ServicoAdapterMapper::servicoOutputToServicoResponse)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Retorna um serviço específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        var output = buscarServicoPorIdUseCase.executar(id);
        var response = ServicoAdapterMapper.servicoOutputToServicoResponse(output);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar serviços por status", description = "Retorna uma lista de serviços filtrados por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços filtrada por status retornada com sucesso")
    })
    public ResponseEntity<PageResult<ServicoResponseDTO>> listaServicosPorStatus(@PathVariable StatusServico status, Pageable pageable) {
        PaginationRequest pagination = new PaginationRequest(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        return ResponseEntity.ok(
            listarServicosPorStatusUseCase.executar(status, pagination)
                .map(ServicoAdapterMapper::servicoOutputToServicoResponse)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualiza um serviço existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ServicoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ServicoRequestDTO request) {
        var input = ServicoAdapterMapper.atualizarServicoRequestToAtualizarServicoInput(request);
        var output = atualizarServicoUseCase.executar(id, input);
        var response = ServicoAdapterMapper.servicoOutputToServicoResponse(output);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço", description = "Deleta um serviço pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
