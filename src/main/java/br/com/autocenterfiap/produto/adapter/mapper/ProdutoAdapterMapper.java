package br.com.autocenterfiap.produto.adapter.mapper;

import br.com.autocenterfiap.produto.adapter.in.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.ProdutoResponseDTO;
import br.com.autocenterfiap.produto.application.dto.AtualizarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.CriarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.MovimentacaoEstoqueInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;

public class ProdutoAdapterMapper {

    public static CriarProdutoInput toCriarInput(ProdutoRequestDTO request) {
        if (request == null) return null;

        return CriarProdutoInput.builder()
                .nome(request.nome())
                .codigo(request.codigo())
                .descricao(request.descricao())
                .unidadeMedida(request.unidadeMedida())
                .precoUnitario(request.precoUnitario())
                .quantidadeEstoque(request.quantidadeEstoque())
                .estoqueMinimo(request.estoqueMinimo())
                .categoria(request.categoria())
                .tipo(request.tipo())
                .build();
    }

    public static AtualizarProdutoInput toAtualizarInput(ProdutoRequestDTO request) {
        if (request == null) return null;

        return AtualizarProdutoInput.builder()
                .nome(request.nome())
                .codigo(request.codigo())
                .descricao(request.descricao())
                .unidadeMedida(request.unidadeMedida())
                .precoUnitario(request.precoUnitario())
                .quantidadeEstoque(request.quantidadeEstoque())
                .estoqueMinimo(request.estoqueMinimo())
                .categoria(request.categoria())
                .tipo(request.tipo())
                .build();
    }

    public static MovimentacaoEstoqueInput toMovimentacaoInput(MovimentacaoEstoqueDTO request) {
        if (request == null) return null;

        return MovimentacaoEstoqueInput.builder()
                .quantidade(request.quantidade())
                .build();
    }

    public static ProdutoResponseDTO toResponse(ProdutoOutput output) {
        return ProdutoResponseDTO.from(output);
    }
}
