package br.com.autocenterfiap.cliente.domain.enums;

public enum StatusCliente {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    BLOQUEADO("Bloqueado");

    private final String descricao;

    StatusCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeAutenticar() {
        return this == ATIVO;
    }
}
