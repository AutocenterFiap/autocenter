package br.com.autocenterfiap.peca.exception;

public class PecaNaoEncontradaException extends RuntimeException {

    public PecaNaoEncontradaException(Long id) {
        super("Peça não encontrada com o ID: " + id);
    }
}