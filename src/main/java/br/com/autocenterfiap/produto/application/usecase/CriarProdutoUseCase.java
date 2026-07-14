package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.CriarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.mapper.ProdutoApplicationMapper;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.CodigoJaCadastradoException;

import java.time.LocalDateTime;

public class CriarProdutoUseCase {

    private final ProdutoRepositoryPort repositoryPort;

    public CriarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ProdutoOutput executar(CriarProdutoInput input) {
        if (repositoryPort.existsByCodigo(input.getCodigo())) {
            throw new CodigoJaCadastradoException(input.getCodigo());
        }

        Produto produto = ProdutoApplicationMapper.toEntity(input);
        produto.validarDominio();

        produto.setDataCriacao(LocalDateTime.now());
        produto.setDataUltimaAtualizacao(LocalDateTime.now());

        Produto produtoSalvo = repositoryPort.salvar(produto);
        return ProdutoApplicationMapper.toOutput(produtoSalvo);
    }
}
