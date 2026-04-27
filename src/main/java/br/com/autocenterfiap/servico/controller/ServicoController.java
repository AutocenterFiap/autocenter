package br.com.autocenterfiap.servico.controller;

import br.com.autocenterfiap.servico.dto.ServicoDto;
import br.com.autocenterfiap.servico.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/api/servicos")
@Tag(name = "Serviços", description = "API para gerenciamento de serviços da oficina")
public class ServicoController {

    @Autowired
    private ServicoService service;

    @PostMapping
    @Operation(summary = "Criar um novo serviço", description = "Cria um novo serviço na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ServicoResponseDTO> criar(@RequestBody ServicoDto servico) {
        return ResponseEntity
                .status(CREATED)
                .body(service.criar(servico));
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso")
    })
    public ResponseEntity<Page<ServicoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Retorna um serviço específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar serviços por status", description = "Retorna uma lista de serviços filtrados por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços filtrada por status retornada com sucesso")
    })
    public ResponseEntity<Page<ServicoResponseDTO>> listaServicosPorStatus(@PathVariable StatusServico status, Pageable pageable) {
        return ResponseEntity.ok(service.listaServicosPorStatus(status, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualiza um serviço existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ServicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody ServicoDto servico) {
        return ResponseEntity.ok(service.atualizar(id, servico));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço", description = "Deleta um serviço pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
