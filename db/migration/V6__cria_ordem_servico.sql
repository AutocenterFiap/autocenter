-- ========================================
-- Migration: V6__cria_ordem_servico
-- Descrição: cria ordem servico
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================
CREATE TABLE ordem_servico (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
numero_ordem_servico BIGINT UNIQUE,
status_os VARCHAR(30) NOT NULL,
valor_total NUMERIC(15,2) NOT NULL,
veiculo_id BIGINT NOT NULL,
cliente_id BIGINT NOT NULL,
data_criacao TIMESTAMP,
data_ultima_atualizacao TIMESTAMP,
CONSTRAINT fk_ordem_servico_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
CONSTRAINT fk_ordem_servico_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE os_item_produto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    -- produto_id BIGINT NOT NULL, (comentado temporariamente)
    valor_item_produto NUMERIC(15,2) NOT NULL,
    quantidade_item BIGINT NOT NULL,
    data_criacao TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_item_produto_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
    -- CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produto(id) (comentado temporariamente)
);

-- Tabela os_item_servico
CREATE TABLE os_item_servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    -- servico_id BIGINT NOT NULL, (comentado temporariamente)
    valor_item_servico NUMERIC(15,2) NOT NULL,
    status_servico VARCHAR(30) NOT NULL,
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP,
    data_criacao TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_item_servico_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
    -- CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servico(id) (comentado temporariamente)
);

-- Tabela orcamento
CREATE TABLE orcamento (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
ordem_servico_id BIGINT NOT NULL,
valor_total NUMERIC(15,2),
status_orcamento VARCHAR(30),
data_criacao TIMESTAMP NOT NULL,
data_ultima_atualizacao TIMESTAMP,
CONSTRAINT fk_orcamento_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
);

insert into ordem_servico (
numero_ordem_servico,
status_os,
valor_total,
veiculo_id,
cliente_id,
data_criacao,
data_ultima_atualizacao)
values
    (1,
     'AGUARDANDO_APROVACAO',
     100.55,
     1,
     1,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);