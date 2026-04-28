-- ========================================
-- Migration: V2__criar_tabela_veiculos
-- Descrição: Cria a tabela de veículos da oficina
-- Data: 2026-04-09
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE veiculos (
    -- Identificação
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Dados do Veículo
    placa VARCHAR(10) NOT NULL UNIQUE,
    chassi VARCHAR(17),
    renavam VARCHAR(11),
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano_fabricacao INT,
    ano_modelo INT,
    cor VARCHAR(50),
    quilometragem BIGINT,

    -- Enum (salvo como STRING)
    tipo_combustivel VARCHAR(20) NOT NULL,
    categoria_veiculo VARCHAR(20) NOT NULL,

    -- Auditoria
    data_cadastro TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_placa ON veiculos(placa);
CREATE INDEX idx_renavam ON veiculos(renavam);
CREATE INDEX idx_marca_modelo ON veiculos(marca, modelo);