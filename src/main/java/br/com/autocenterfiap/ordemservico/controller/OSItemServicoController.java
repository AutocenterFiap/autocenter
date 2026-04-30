package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.dto.OSItemServicoRequestDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.service.OSItemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/ordem-servico/{ordemServicoId}/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços na OS", description = "Endpoints para gerenciamento de serviços em ordens de serviço")
public class OSItemServicoController {

    private final OSItemServicoService osItemServicoService;

    @Operation(
            summary = "Listar serviços da ordem de serviço",
            description = "Retorna todos os serviços vinculados a uma ordem de serviço específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OSItemServicoResponseDTO>> listarServicos(
            @Parameter(description = "ID da ordem de serviço", required = true, example = "1")
            @PathVariable Long ordemServicoId
    ) {
        List<OSItemServicoResponseDTO> servicos = osItemServicoService.listarPorOS(ordemServicoId);
        return ResponseEntity.ok(servicos);
    }

    @Operation(
            summary = "Adicionar serviço na ordem de serviço",
            description = "Vincula um novo serviço a uma ordem de serviço existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou serviço inativo", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço ou serviço não encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OSItemServicoResponseDTO> adicionarServico(
            @Parameter(description = "ID da ordem de serviço", required = true, example = "1")
            @PathVariable Long ordemServicoId,
            @Parameter(description = "Dados do serviço a ser adicionado", required = true)
            @Valid @RequestBody OSItemServicoRequestDTO dto
    ) {
        OSItemServicoResponseDTO response = osItemServicoService.adicionarServicoNaOS(ordemServicoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Iniciar execução do serviço",
            description = "Marca o serviço como 'EXECUTANDO' e registra a data/hora de início"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço iniciado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado na ordem de serviço", content = @Content)
    })
    @PatchMapping("/{servicoId}/iniciar")
    public ResponseEntity<OSItemServicoResponseDTO> iniciarServico(
            @Parameter(description = "ID da ordem de serviço", required = true, example = "1")
            @PathVariable Long ordemServicoId,
            @Parameter(description = "ID do serviço", required = true, example = "5")
            @PathVariable Long servicoId
    ) {
        OSItemServicoResponseDTO response = osItemServicoService.iniciarServico(ordemServicoId, servicoId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Finalizar execução do serviço",
            description = "Marca o serviço como 'FINALIZADO' e registra a data/hora de término"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço finalizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado na ordem de serviço", content = @Content)
    })
    @PatchMapping("/{servicoId}/finalizar")
    public ResponseEntity<OSItemServicoResponseDTO> finalizarServico(
            @Parameter(description = "ID da ordem de serviço", required = true, example = "1")
            @PathVariable Long ordemServicoId,
            @Parameter(description = "ID do serviço", required = true, example = "5")
            @PathVariable Long servicoId
    ) {
        OSItemServicoResponseDTO response = osItemServicoService.finalizarServico(ordemServicoId, servicoId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remover serviço da ordem de serviço",
            description = "Remove um serviço específico da ordem de serviço"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço removido com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado na ordem de serviço", content = @Content)
    })
    @DeleteMapping("/{servicoId}")
    public ResponseEntity<Void> removerServico(
            @Parameter(description = "ID da ordem de serviço", required = true, example = "1")
            @PathVariable Long ordemServicoId,
            @Parameter(description = "ID do serviço", required = true, example = "5")
            @PathVariable Long servicoId
    ) {
        osItemServicoService.removerServicoDaOS(ordemServicoId, servicoId);
        return ResponseEntity.noContent().build();
    }

}
