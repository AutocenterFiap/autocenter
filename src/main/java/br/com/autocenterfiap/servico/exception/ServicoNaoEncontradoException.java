package br.com.autocenterfiap.servico.exception;

public class ServicoNaoEncontradoException extends RuntimeException {
    public ServicoNaoEncontradoException(String message) {
        super(message);
    }

}