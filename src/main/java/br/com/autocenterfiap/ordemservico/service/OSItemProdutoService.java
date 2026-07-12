package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OSItemProdutoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoRequestDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoResponseDTO;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository.ProdutoJpaRepository;
import br.com.autocenterfiap.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OSItemProdutoService {

    private final OSItemProdutoRepository osItemProdutoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ProdutoJpaRepository produtoJpaRepository;

    public List<OSItemProdutoResponseDTO> listarPorOS(Long ordemServicoId) {
        return osItemProdutoRepository.findByOrdemServicoId(ordemServicoId)
                .stream()
                .map(OSItemProdutoResponseDTO::from)
                .toList();
    }

    @Transactional
    public OSItemProdutoResponseDTO adicionarProdutoNaOS(Long ordemServicoId, OSItemProdutoRequestDTO dto) {
        ProdutoJpaEntity produto = produtoJpaRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(dto.produtoId()));

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

        // Recalcula o valor total da OS após adicionar o produto
        ordemServico.getOsItensProdutos().add(item);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        return OSItemProdutoResponseDTO.from(osItemProdutoRepository.save(item));
    }

    @Transactional
    public OSItemProdutoResponseDTO atualizarQuantidade(Long ordemServicoId, Long produtoId, OSItemProdutoRequestDTO dto) {
        OSItemProduto item = osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));
        

        ProdutoJpaEntity produto = item.getProduto();
        int diferencaQuantidade = dto.quantidade() - item.getQuantidade();

        if (diferencaQuantidade > 0) {
            // Aumentou a quantidade: reservar mais estoque
            produto.decrementarEstoque(diferencaQuantidade);
        } else if (diferencaQuantidade < 0) {
            // Reduziu a quantidade: devolver ao estoque
            produto.incrementarEstoque(Math.abs(diferencaQuantidade));
        }

        item.setQuantidade(dto.quantidade());

        // Recalcula o valor total da OS após atualizar a quantidade
        OrdemServico os = item.getOrdemServico();
        os.setValorTotal(Util.calcularValorTotal(os));

        return OSItemProdutoResponseDTO.from(item);
    }

    @Transactional
    public void removerProdutoDaOS(Long ordemServicoId, Long produtoId) {
        OSItemProduto item = osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        // Devolve a quantidade ao estoque
        item.getProduto().incrementarEstoque(item.getQuantidade());

        OrdemServico os = item.getOrdemServico();
        os.getOsItensProdutos().remove(item);

        // Recalcula o valor total da OS
        os.setValorTotal(Util.calcularValorTotal(os));
    }

}
