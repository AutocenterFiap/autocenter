package br.com.autocenterfiap.cliente.adapter.in;

import br.com.autocenterfiap.cliente.adapter.in.dto.ClienteRequestDTO;
import br.com.autocenterfiap.cliente.adapter.in.dto.ClienteResponseDTO;
import br.com.autocenterfiap.cliente.adapter.mapper.ClienteAdapterMapper;
import br.com.autocenterfiap.cliente.application.dto.PaginationRequest;
import br.com.autocenterfiap.cliente.application.usecase.*;
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
@RequestMapping("/v1/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes da oficina")
public class ClienteController {

    private final CriarClienteUseCase criarClienteUseCase;
    private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    private final BuscarClientePorDocumentoUseCase buscarClientePorDocumentoUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;

    public ClienteController(
        CriarClienteUseCase criarClienteUseCase,
        BuscarClientePorIdUseCase buscarClientePorIdUseCase,
        BuscarClientePorDocumentoUseCase buscarClientePorDocumentoUseCase,
        ListarClientesUseCase listarClientesUseCase,
        AtualizarClienteUseCase atualizarClienteUseCase,
        DeletarClienteUseCase deletarClienteUseCase
    ) {
        this.criarClienteUseCase = criarClienteUseCase;
        this.buscarClientePorIdUseCase = buscarClientePorIdUseCase;
        this.buscarClientePorDocumentoUseCase = buscarClientePorDocumentoUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.deletarClienteUseCase = deletarClienteUseCase;
    }

    @Operation(
        summary = "Listar todos os clientes",
        description = "Retorna uma lista paginada com todos os clientes cadastrados no sistema. " +
                "Por padrão retorna 20 clientes por página, ordenados por ID de forma crescente."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de clientes retornada com sucesso"
    )
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarTodos(Pageable pageable) {
        PaginationRequest pagination = new PaginationRequest(
            pageable.getPageNumber(),
            pageable.getPageSize()
        );

        var pageResultOutput = listarClientesUseCase.executar(pagination);

        Page<ClienteResponseDTO> response = new PageImpl<>(
            pageResultOutput.getContent().stream()
                .map(ClienteAdapterMapper::clienteOutputToClienteResponse)
                .toList(),
            pageable,
            pageResultOutput.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna um cliente específico pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
        @Parameter(description = "ID do cliente a ser buscado", required = true)
        @PathVariable Long id
    ) {
        var clienteOutput = buscarClientePorIdUseCase.executar(id);
        var response = ClienteAdapterMapper.clienteOutputToClienteResponse(clienteOutput);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Buscar cliente por documento",
        description = "Retorna um cliente específico pelo seu documento (CPF ou CNPJ)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado"
        )
    })
    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteResponseDTO> buscarPorDocumento(
        @Parameter(description = "Documento do cliente - CPF (11 dígitos) ou CNPJ (14 dígitos)", required = true, example = "12345678901")
        @PathVariable String documento
    ) {
        var clienteOutput = buscarClientePorDocumentoUseCase.executar(documento);
        var response = ClienteAdapterMapper.clienteOutputToClienteResponse(clienteOutput);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Criar novo cliente",
        description = "Cadastra um novo cliente no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "CPF ou email já cadastrado"
        )
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(
        @Parameter(description = "Dados do cliente a ser criado", required = true)
        @Valid @RequestBody ClienteRequestDTO clienteRequest
    ) {
        var input = ClienteAdapterMapper.criarClienteRequestToCriarClienteInput(clienteRequest);
        var output = criarClienteUseCase.executar(input);
        var response = ClienteAdapterMapper.clienteOutputToClienteResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Atualizar cliente existente",
        description = "Atualiza os dados de um cliente existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email já cadastrado para outro cliente"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
        @Parameter(description = "ID do cliente a ser atualizado", required = true)
        @PathVariable Long id,
        @Parameter(description = "Novos dados do cliente", required = true)
        @Valid @RequestBody ClienteRequestDTO clienteRequest
    ) {
        var input = ClienteAdapterMapper.atualizarClienteRequestToAtualizarClienteInput(clienteRequest);
        var output = atualizarClienteUseCase.executar(id, input);
        var response = ClienteAdapterMapper.clienteOutputToClienteResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Deletar cliente",
        description = "Remove um cliente do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Cliente deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
        @Parameter(description = "ID do cliente a ser deletado", required = true)
        @PathVariable Long id
    ) {
        deletarClienteUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}

