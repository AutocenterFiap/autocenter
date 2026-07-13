package br.com.autocenterfiap.orcamento.application.exception;

import br.com.autocenterfiap.security.exception.InformacaoNaoEncontradaException;

public class OrcamentoNaoEncontradoException extends InformacaoNaoEncontradaException {

    public OrcamentoNaoEncontradoException(Long id) {
        super("Orcamento não encontrado com o ID: " + id);
    }
    public OrcamentoNaoEncontradoException(String message) {
        super(message);
    }
}
