package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.repository.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.repository.entity.OrdemServico;
import br.com.autocenterfiap.produto.dto.OSItemProdutoRequestDTO;
import br.com.autocenterfiap.produto.dto.OSItemProdutoResponseDTO;
import br.com.autocenterfiap.produto.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.model.Produto;
import br.com.autocenterfiap.ordemservico.repository.OSItemProdutoRepository;
import br.com.autocenterfiap.produto.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OSItemProdutoService {

    private final OSItemProdutoRepository osItemProdutoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ProdutoService produtoService;

    public List<OSItemProdutoResponseDTO> listarPorOS(Long ordemServicoId) {
        return osItemProdutoRepository.findByOrdemServicoId(ordemServicoId)
                .stream()
                .map(OSItemProdutoResponseDTO::from)
                .toList();
    }

    @Transactional
    public OSItemProdutoResponseDTO adicionarProdutoNaOS(Long ordemServicoId, OSItemProdutoRequestDTO dto) {
        Produto produto = produtoService.buscarOuLancarExcecao(dto.produtoId());

        if (!produto.getAtivo()) {
            throw new ProdutoInativoException(produto.getCodigo());
        }

        // Decrementa o estoque imediatamente (reserva o produto)
        produto.decrementarEstoque(dto.quantidade());

        OrdemServico ordemServico = ordemServicoRepository.getReferenceById(ordemServicoId);

        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());

        return OSItemProdutoResponseDTO.from(osItemProdutoRepository.save(item));
    }

    @Transactional
    public OSItemProdutoResponseDTO atualizarQuantidade(Long ordemServicoId, Long produtoId, OSItemProdutoRequestDTO dto) {
        OSItemProduto item = osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        Produto produto = item.getProduto();
        int diferencaQuantidade = dto.quantidade() - item.getQuantidade();

        if (diferencaQuantidade > 0) {
            // Aumentou a quantidade: reservar mais estoque
            produto.decrementarEstoque(diferencaQuantidade);
        } else if (diferencaQuantidade < 0) {
            // Reduziu a quantidade: devolver ao estoque
            produto.incrementarEstoque(Math.abs(diferencaQuantidade));
        }

        item.setQuantidade(dto.quantidade());
        return OSItemProdutoResponseDTO.from(osItemProdutoRepository.save(item));
    }

    @Transactional
    public void removerProdutoDaOS(Long ordemServicoId, Long produtoId) {
        OSItemProduto item = osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        // Devolve a quantidade ao estoque
        item.getProduto().incrementarEstoque(item.getQuantidade());

        osItemProdutoRepository.delete(item);
    }
}
