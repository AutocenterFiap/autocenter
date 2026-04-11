-- ========================================
-- Migration: V2__inserir_dados_iniciais_clientes
-- Descrição: Insere clientes de exemplo (PF e PJ) para testes
-- Data: 2026-04-08
-- Autor: Auto Center FIAP Team
-- ========================================

-- Cliente 1: João da Silva (Pessoa Física)
INSERT INTO clientes (
    nome, tipo_cliente, documento, email, telefone, data_nascimento,
    cep, logradouro, numero, complemento, bairro, cidade, estado
) VALUES (
    'João da Silva',
    'PESSOA_FISICA',
    '12345678901',
    'joao.silva@email.com',
    '11987654321',
    '1990-05-15',
    '01310100',
    'Avenida Paulista',
    '1578',
    'Andar 5',
    'Bela Vista',
    'São Paulo',
    'SP'
);

-- Cliente 2: Maria Santos (Pessoa Física)
INSERT INTO clientes (
    nome, tipo_cliente, documento, email, telefone, data_nascimento,
    cep, logradouro, numero, complemento, bairro, cidade, estado
) VALUES (
    'Maria Santos',
    'PESSOA_FISICA',
    '98765432109',
    'maria.santos@email.com',
    '11876543210',
    '1985-08-20',
    '04567890',
    'Rua das Flores',
    '100',
    'Apto 201',
    'Jardim Paulista',
    'São Paulo',
    'SP'
);

-- Cliente 3: Pedro Oliveira (Pessoa Física)
INSERT INTO clientes (
    nome, tipo_cliente, documento, email, telefone, data_nascimento,
    cep, logradouro, numero, complemento, bairro, cidade, estado
) VALUES (
    'Pedro Oliveira',
    'PESSOA_FISICA',
    '11122233344',
    'pedro.oliveira@email.com',
    '11965432100',
    '1995-03-10',
    '02345678',
    'Rua Augusta',
    '500',
    NULL,
    'Consolação',
    'São Paulo',
    'SP'
);

-- Cliente 4: Tech Solutions LTDA (Pessoa Jurídica)
INSERT INTO clientes (
    nome, tipo_cliente, documento, email, telefone,
    cep, logradouro, numero, complemento, bairro, cidade, estado
) VALUES (
    'Tech Solutions LTDA',
    'PESSOA_JURIDICA',
    '12345678000195',
    'contato@techsolutions.com',
    '1133334444',
    '01310100',
    'Avenida Paulista',
    '2000',
    'Sala 1001',
    'Bela Vista',
    'São Paulo',
    'SP'
);
