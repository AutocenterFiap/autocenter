package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.AtualizarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.mapper.ProdutoApplicationMapper;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;

public class AtualizarProdutoUseCase {

    private final ProdutoRepositoryPort repositoryPort;

    public AtualizarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ProdutoOutput executar(Long id, AtualizarProdutoInput input) {
        Produto produto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        if (repositoryPort.existsByCodigoAndIdNot(input.getCodigo(), id)) {
            throw new CodigoJaCadastradoException(input.getCodigo());
        }

        produto.atualizarDados(
                input.getNome(),
                input.getCodigo(),
                input.getDescricao(),
                input.getUnidadeMedida(),
                input.getPrecoUnitario(),
                input.getQuantidadeEstoque(),
                input.getEstoqueMinimo(),
                input.getCategoria(),
                input.getTipo()
        );

        Produto produtoSalvo = repositoryPort.salvar(produto);
        return ProdutoApplicationMapper.toOutput(produtoSalvo);
    }
}
