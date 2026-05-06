package br.com.autocenterfiap.cliente.controller;

import br.com.autocenterfiap.cliente.dto.ClienteDTO;
import br.com.autocenterfiap.cliente.dto.ClienteResponseDTO;
import br.com.autocenterfiap.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes da oficina")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

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
        Page<ClienteResponseDTO> clientes = clienteService.listarTodos(pageable);
        return ResponseEntity.ok(clientes);
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
        return clienteService.buscarPorId(id);
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
        return clienteService.buscarPorDocumento(documento);
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
            @Valid @RequestBody ClienteDTO clienteDTO
    ) {
        ClienteResponseDTO cliente = clienteService.criar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
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
            @Valid @RequestBody ClienteDTO clienteDTO
    ) {
        ClienteResponseDTO cliente = clienteService.atualizar(id, clienteDTO);
        return ResponseEntity.ok(cliente);
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
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
