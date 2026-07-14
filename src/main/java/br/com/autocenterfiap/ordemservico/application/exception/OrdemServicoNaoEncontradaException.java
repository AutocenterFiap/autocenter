package br.com.autocenterfiap.ordemservico.application.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {
    public OrdemServicoNaoEncontradaException(String message) {
        super(message);
    }
}
