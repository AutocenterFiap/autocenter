package br.com.autocenterfiap.peca.exception;

public class CodigoJaCadastradoException extends RuntimeException {

    public CodigoJaCadastradoException(String codigo) {
        super("Já existe uma peça cadastrada com o código: " + codigo);
    }
}