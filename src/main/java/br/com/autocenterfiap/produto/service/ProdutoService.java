package br.com.autocenterfiap.produto.service;

import br.com.autocenterfiap.produto.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.dto.ProdutoResponseDTO;
import br.com.autocenterfiap.produto.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.produto.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.model.Produto;
import br.com.autocenterfiap.produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<ProdutoResponseDTO> listar(String categoria, String busca) {
        return produtoRepository.buscarComFiltros(categoria, busca, true)
                .stream()
                .map(ProdutoResponseDTO::from)
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = buscarOuLancarExcecao(id);
        return ProdutoResponseDTO.from(produto);
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        if (produtoRepository.existsByCodigo(dto.codigo())) {
            throw new CodigoJaCadastradoException(dto.codigo());
        }
        Produto produto = new Produto(dto);
        return ProdutoResponseDTO.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscarOuLancarExcecao(id);

        if (produtoRepository.existsByCodigoAndIdNot(dto.codigo(), id)) {
            throw new CodigoJaCadastradoException(dto.codigo());
        }

        produto.atualizarDados(dto);
        return ProdutoResponseDTO.from(produtoRepository.save(produto));
    }

    @Transactional
    public void desativar(Long id) {
        Produto produto = buscarOuLancarExcecao(id);
        produto.desativar();
        produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoResponseDTO adicionarEstoque(Long id, MovimentacaoEstoqueDTO dto) {
        Produto produto = buscarOuLancarExcecao(id);
        produto.incrementarEstoque(dto.quantidade());
        return ProdutoResponseDTO.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO removerEstoque(Long id, MovimentacaoEstoqueDTO dto) {
        Produto produto = buscarOuLancarExcecao(id);
        produto.decrementarEstoque(dto.quantidade());
        return ProdutoResponseDTO.from(produtoRepository.save(produto));
    }

    public List<ProdutoResponseDTO> listarProdutosComProblemaDeEstoque() {
        List<Produto> semEstoque = produtoRepository.findProdutosSemEstoque();
        List<Produto> estoqueBaixo = produtoRepository.findProdutosComEstoqueBaixo();

        return java.util.stream.Stream.concat(semEstoque.stream(), estoqueBaixo.stream())
                .distinct()
                .map(ProdutoResponseDTO::from)
                .toList();
    }

    public Produto buscarOuLancarExcecao(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }
}
