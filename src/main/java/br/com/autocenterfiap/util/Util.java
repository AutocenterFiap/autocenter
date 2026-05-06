package br.com.autocenterfiap.util;

import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;

import java.math.BigDecimal;

public class Util {

    public static BigDecimal calcularValorTotal(OrdemServico os) {
        BigDecimal valorTotal = BigDecimal.ZERO;

        BigDecimal valorTotalPecas = os.getOsItensProdutos()
                .stream().map(osItemProduto ->
                        osItemProduto.getPrecoUnitarioNoMomento()
                                .multiply(BigDecimal.valueOf(osItemProduto.getQuantidade()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalServico = os.getOsItensServicos()
                .stream().map(OSItemServico::getValorItemServico).reduce(BigDecimal.ZERO, BigDecimal::add);

        return valorTotal.add(valorTotalPecas).add(valorTotalServico);
    }

}
