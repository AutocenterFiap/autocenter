-- ========================================
-- Migration: V4__inserir_dados_iniciais_veiculos
-- Descrição: Insere veículos de exemplo para testes
-- Data: 2026-04-09
-- Autor: Auto Center FIAP Team
-- ========================================

-- Veículo 1: Toyota Corolla
INSERT INTO veiculos (
    placa, chassi, renavam, marca, modelo,
    ano_fabricacao, ano_modelo, cor, quilometragem,
    tipo_combustivel, categoria_veiculo,
    data_cadastro, data_ultima_atualizacao
) VALUES (
    'ABC1D23',
    '9BWZZZ377VT004251',
    '82106426707',
    'Toyota',
    'Corolla',
    2020,
    2021,
    'Preto',
    45000,
    'FLEX',
    'CARRO',
    NOW(),
    NOW()
);

-- Veículo 2: Honda Civic
INSERT INTO veiculos (
    placa, chassi, renavam, marca, modelo,
    ano_fabricacao, ano_modelo, cor, quilometragem,
    tipo_combustivel, categoria_veiculo,
    data_cadastro, data_ultima_atualizacao
) VALUES (
    'DEF2G45',
    '8AFZZZ54ZMJ123456',
    '85996858465',
    'Honda',
    'Civic',
    2019,
    2020,
    'Branco',
    60000,
    'FLEX',
    'CARRO',
    NOW(),
    NOW()
);

-- Veículo 3: Ford Ranger
INSERT INTO veiculos (
    placa, chassi, renavam, marca, modelo,
    ano_fabricacao, ano_modelo, cor, quilometragem,
    tipo_combustivel, categoria_veiculo,
    data_cadastro, data_ultima_atualizacao
) VALUES (
    'GHI3J67',
    '3FAHP0HA6AR298374',
    '17059907791',
    'Ford',
    'Ranger',
    2018,
    2019,
    'Prata',
    85000,
    'DIESEL',
    'CARRO',
    NOW(),
    NOW()
);

-- Veículo 4: Yamaha Fazer 250
INSERT INTO veiculos (
    placa, chassi, renavam, marca, modelo,
    ano_fabricacao, ano_modelo, cor, quilometragem,
    tipo_combustivel, categoria_veiculo,
    data_cadastro, data_ultima_atualizacao
) VALUES (
    'JKL4M89',
    '9C6RG5020N0001234',
    '94204304510',
    'Yamaha',
    'Fazer 250',
    2022,
    2022,
    'Azul',
    12000,
    'GASOLINA',
    'MOTO',
    NOW(),
    NOW()
);