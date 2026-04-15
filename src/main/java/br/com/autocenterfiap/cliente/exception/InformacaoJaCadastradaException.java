package br.com.autocenterfiap.cliente.exception;

public class InformacaoJaCadastradaException extends RuntimeException {

    public InformacaoJaCadastradaException(String mensagem) {
        super(mensagem);
    }

    public InformacaoJaCadastradaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
