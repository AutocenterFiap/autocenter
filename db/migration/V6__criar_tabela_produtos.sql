-- ========================================
-- Migration: V6__criar_tabela_produtos
-- Descrição: Cria tabelas de produtos/insumos e itens de produto na OS
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE produtos (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome                  VARCHAR(255)   NOT NULL,
    codigo                VARCHAR(100)   NOT NULL UNIQUE,
    descricao             VARCHAR(1000),
    unidade_medida        VARCHAR(10)    NOT NULL,
    preco_unitario        NUMERIC(15, 2) NOT NULL,
    quantidade_estoque    INT            NOT NULL,
    estoque_minimo        INT            NOT NULL,
    categoria             VARCHAR(100)   NOT NULL,
    tipo                  VARCHAR(10)    NOT NULL,
    ativo                 BOOLEAN        NOT NULL DEFAULT TRUE,
    data_criacao          TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP
);

CREATE INDEX idx_produtos_codigo    ON produtos (codigo);
CREATE INDEX idx_produtos_categoria ON produtos (categoria);
CREATE INDEX idx_produtos_ativo     ON produtos (ativo);

-- Tabela de itens de produto vinculados a uma Ordem de Serviço
-- NOTA: a FK para ordem_servico será adicionada quando essa tabela existir
CREATE TABLE os_item_produto (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id          BIGINT         NOT NULL,
    produto_id                BIGINT         NOT NULL,
    quantidade                INT            NOT NULL,
    preco_unitario_no_momento NUMERIC(15, 2) NOT NULL,
    data_criacao              TIMESTAMP,
    data_ultima_atualizacao   TIMESTAMP,
    CONSTRAINT fk_os_item_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
);

CREATE INDEX idx_os_item_produto_os      ON os_item_produto (ordem_servico_id);
CREATE INDEX idx_os_item_produto_produto ON os_item_produto (produto_id);
