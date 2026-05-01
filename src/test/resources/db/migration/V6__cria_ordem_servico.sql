-- ========================================
-- Migration: V6__cria_ordem_servico
-- Descrição: cria ordem servico
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================
CREATE TABLE ordem_servico (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
numero_ordem_servico BIGINT UNIQUE,
status_os VARCHAR(30),
valor_total NUMERIC(15,2),
veiculo_id BIGINT,
cliente_id BIGINT,
data_criacao TIMESTAMP,
data_ultima_atualizacao TIMESTAMP,
CONSTRAINT fk_ordem_servico_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
CONSTRAINT fk_ordem_servico_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE os_item_produto
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id          BIGINT         NOT NULL,
    produto_id                BIGINT         NOT NULL,
    quantidade                INT            NOT NULL,
    preco_unitario_no_momento NUMERIC(15, 2) NOT NULL,
    data_criacao              TIMESTAMP,
    data_ultima_atualizacao   TIMESTAMP,
    CONSTRAINT fk_item_produto_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE,
    CONSTRAINT fk_os_item_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
);
CREATE INDEX idx_os_item_produto_os      ON os_item_produto (ordem_servico_id);
CREATE INDEX idx_os_item_produto_produto ON os_item_produto (produto_id);

-- Tabela os_item_servico
CREATE TABLE os_item_servico (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
ordem_servico_id BIGINT NOT NULL,
servico_id BIGINT NOT NULL,
valor_item_servico NUMERIC(15,2) NOT NULL,
status_servico VARCHAR(30) NOT NULL,
data_hora_inicio TIMESTAMP NOT NULL,
data_hora_fim TIMESTAMP,
data_criacao TIMESTAMP,
data_ultima_atualizacao TIMESTAMP,
CONSTRAINT fk_item_servico_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
,CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servicos(id)
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