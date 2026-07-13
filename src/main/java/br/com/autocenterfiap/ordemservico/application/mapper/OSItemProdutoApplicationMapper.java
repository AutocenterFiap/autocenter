package br.com.autocenterfiap.ordemservico.application.mapper;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoResponseDTO;

public class OSItemProdutoApplicationMapper {
    public static OSItemProdutoOutput toOutput(OSItemProduto osItemProduto) {
        if (osItemProduto == null) return null;

        return new OSItemProdutoOutput(
                osItemProduto.getId(),
                osItemProduto.getOrdemServico().getId(),
                osItemProduto.getProduto().getId(),
                osItemProduto.getProduto().getCodigo(),
                osItemProduto.getProduto().getNome(),
                osItemProduto.getQuantidade(),
                osItemProduto.getPrecoUnitarioNoMomento(),
                osItemProduto.calcularSubtotal(),
                osItemProduto.getDataCriacao()
        );
    }

    public static OSItemProdutoResponseDTO outputToOSItemProdutoResponseDTO(OSItemProdutoOutput osItemProdutoOutput) {
        if (osItemProdutoOutput == null) return null;

        return new OSItemProdutoResponseDTO(
                osItemProdutoOutput.id(),
                osItemProdutoOutput.ordemServicoId(),
                osItemProdutoOutput.produtoId(),
                osItemProdutoOutput.codigoProduto(),
                osItemProdutoOutput.nomeProduto(),
                osItemProdutoOutput.quantidade(),
                osItemProdutoOutput.precoUnitarioNoMomento(),
                osItemProdutoOutput.subtotal(),
                osItemProdutoOutput.dataCriacao()
        );
    }
}
