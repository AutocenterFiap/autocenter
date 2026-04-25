package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.service.OrdemServicoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ordem-servicos")
@Tag(name = "Ordens de Serviço", description = "API para gerenciamento de ordens de serviço da oficina")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(ordemServicoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping("/numero/{numeroOs}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorNumero(@PathVariable Long numeroOs){
        return ResponseEntity.ok(ordemServicoService.buscarPorNumeroOrdemServico(numeroOs));
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(@RequestBody @Valid OrdemServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid OrdemServicoDTO dto) {
        return ResponseEntity.ok(ordemServicoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ordemServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
