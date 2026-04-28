package br.com.autocenterfiap.produto.exception;

public class CodigoJaCadastradoException extends RuntimeException {

    public CodigoJaCadastradoException(String codigo) {
        super("Já existe um produto cadastrado com o código: " + codigo);
    }
}
