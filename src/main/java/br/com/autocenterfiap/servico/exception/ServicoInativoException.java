package br.com.autocenterfiap.servico.exception;

public class ServicoInativoException extends RuntimeException {

    public ServicoInativoException(String descricao) {
        super(String.format("Serviço '%s' está inativo e não pode ser adicionado à ordem de serviço", descricao));
    }
}
