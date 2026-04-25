package br.com.autocenterfiap.ordemservico.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {
    public OrdemServicoNaoEncontradaException(String message) {
        super(message);
    }
}
