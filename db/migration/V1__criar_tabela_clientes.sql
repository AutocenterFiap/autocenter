-- ========================================
-- Migration: V1__criar_tabela_clientes
-- Descrição: Cria a tabela de clientes com suporte a PF (CPF) e PJ (CNPJ)
-- Data: 2026-04-08
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE clientes (
    -- Identificação
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Dados Pessoais/Empresariais
    nome VARCHAR(255) NOT NULL,
    tipo_cliente VARCHAR(20) NOT NULL,
    documento VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE,

    -- Endereço (Embedded)
    cep VARCHAR(8) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20),
    complemento VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL
);

-- Criar índices para melhor performance
CREATE INDEX idx_documento ON clientes(documento);
CREATE INDEX idx_email ON clientes(email);
CREATE INDEX idx_tipo_cliente ON clientes(tipo_cliente);
CREATE INDEX idx_cidade_estado ON clientes(cidade, estado);
