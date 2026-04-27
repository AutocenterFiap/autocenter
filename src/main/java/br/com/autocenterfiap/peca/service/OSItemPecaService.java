package br.com.autocenterfiap.peca.service;

import br.com.autocenterfiap.peca.dto.OSItemPecaRequestDTO;
import br.com.autocenterfiap.peca.dto.OSItemPecaResponseDTO;
import br.com.autocenterfiap.peca.exception.OSItemPecaNaoEncontradoException;
import br.com.autocenterfiap.peca.exception.PecaInativaException;
import br.com.autocenterfiap.peca.model.OSItemPeca;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.OSItemPecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OSItemPecaService {

    private final OSItemPecaRepository osItemPecaRepository;
    private final PecaService pecaService;

    public List<OSItemPecaResponseDTO> listarPorOS(Long ordemServicoId) {
        return osItemPecaRepository.findByOrdemServicoId(ordemServicoId)
                .stream()
                .map(OSItemPecaResponseDTO::from)
                .toList();
    }

    @Transactional
    public OSItemPecaResponseDTO adicionarPecaNaOS(Long ordemServicoId, OSItemPecaRequestDTO dto) {
        Peca peca = pecaService.buscarOuLancarExcecao(dto.pecaId());

        if (!peca.getAtivo()) {
            throw new PecaInativaException(peca.getCodigo());
        }

        // Decrementa o estoque imediatamente (reserva a peça)
        peca.decrementarEstoque(dto.quantidade());

        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(ordemServicoId);
        item.setPeca(peca);
        item.setQuantidade(dto.quantidade());
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());

        return OSItemPecaResponseDTO.from(osItemPecaRepository.save(item));
    }

    @Transactional
    public OSItemPecaResponseDTO atualizarQuantidade(Long ordemServicoId, Long pecaId, OSItemPecaRequestDTO dto) {
        OSItemPeca item = osItemPecaRepository.findByOrdemServicoIdAndPecaId(ordemServicoId, pecaId)
                .orElseThrow(() -> new OSItemPecaNaoEncontradoException(ordemServicoId, pecaId));

        Peca peca = item.getPeca();
        int diferencaQuantidade = dto.quantidade() - item.getQuantidade();

        if (diferencaQuantidade > 0) {
            // Aumentou a quantidade: reservar mais estoque
            peca.decrementarEstoque(diferencaQuantidade);
        } else if (diferencaQuantidade < 0) {
            // Reduziu a quantidade: devolver ao estoque
            peca.incrementarEstoque(Math.abs(diferencaQuantidade));
        }

        item.setQuantidade(dto.quantidade());
        return OSItemPecaResponseDTO.from(osItemPecaRepository.save(item));
    }

    @Transactional
    public void removerPecaDaOS(Long ordemServicoId, Long pecaId) {
        OSItemPeca item = osItemPecaRepository.findByOrdemServicoIdAndPecaId(ordemServicoId, pecaId)
                .orElseThrow(() -> new OSItemPecaNaoEncontradoException(ordemServicoId, pecaId));

        // Devolve a quantidade ao estoque
        item.getPeca().incrementarEstoque(item.getQuantidade());

        osItemPecaRepository.delete(item);
    }
}
