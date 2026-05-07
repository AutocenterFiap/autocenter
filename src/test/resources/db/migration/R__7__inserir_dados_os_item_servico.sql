-- ========================================
-- Migration: V7__inserir_dados_os_item_servico
-- Descrição: Insere dados iniciais para testes de OSItemServico
-- Data: 2026-04-30
-- Autor: Auto Center FIAP Team
-- ========================================

-- Inserir cliente base para OS
INSERT INTO clientes (nome, tipo_cliente, documento, email, telefone,
                      cep, logradouro, numero, bairro, cidade, estado,
                      data_criacao, data_ultima_atualizacao)
VALUES ('Cliente Teste OS', 'PESSOA_FISICA', '11144477735', 'cliente.os@email.com', '11999990000',
        '01310100', 'Avenida Paulista', '1578', 'Bela Vista', 'São Paulo', 'SP',
        NOW(), NOW());

-- Inserir veículo base para OS
INSERT INTO veiculos (placa, chassi, renavam, marca, modelo, ano_fabricacao, ano_modelo,
                      cor, quilometragem, tipo_combustivel, categoria_veiculo,
                      data_cadastro, data_ultima_atualizacao)
VALUES ('TST0001', '9BWZZZ377VT004251', '12345678901', 'Toyota', 'Corolla',
        2020, 2021, 'Prata', 50000, 'FLEX', 'CARRO',
        NOW(), NOW());

-- Inserir serviços para os testes
INSERT INTO servicos (descricao, status, valor, data_criacao, data_ultima_atualizacao)
VALUES ('Troca de óleo', 'ATIVO', 150.00, NOW(), NOW());

INSERT INTO servicos (descricao, status, valor, data_criacao, data_ultima_atualizacao)
VALUES ('Alinhamento', 'ATIVO', 120.00, NOW(), NOW());

INSERT INTO servicos (descricao, status, valor, data_criacao, data_ultima_atualizacao)
VALUES ('Serviço Inativo', 'INATIVO', 80.00, NOW(), NOW());

-- Inserir OS em EM_DIAGNOSTICO (para adicionar/remover serviços)
INSERT INTO ordem_servico (numero_ordem_servico, status_os, valor_total, veiculo_id, cliente_id, data_criacao, data_ultima_atualizacao)
SELECT 1001, 'EM_DIAGNOSTICO', 0.00, v.id, c.id, NOW(), NOW()
FROM veiculos v, clientes c
WHERE v.placa = 'TST0001' AND c.documento = '11144477735';

-- Inserir OS em EM_EXECUCAO (para iniciar serviços)
INSERT INTO ordem_servico (numero_ordem_servico, status_os, valor_total, veiculo_id, cliente_id, data_criacao, data_ultima_atualizacao)
SELECT 1002, 'EM_EXECUCAO', 150.00, v.id, c.id, NOW(), NOW()
FROM veiculos v, clientes c
WHERE v.placa = 'TST0001' AND c.documento = '11144477735';

-- Inserir OS FINALIZADA (para testar status inválido)
INSERT INTO ordem_servico (numero_ordem_servico, status_os, valor_total, veiculo_id, cliente_id, data_criacao, data_ultima_atualizacao)
SELECT 1003, 'FINALIZADA', 150.00, v.id, c.id, NOW(), NOW()
FROM veiculos v, clientes c
WHERE v.placa = 'TST0001' AND c.documento = '11144477735';

-- Inserir item de serviço na OS EM_EXECUCAO (AGUARDANDO_INICIO - para iniciar)
INSERT INTO os_item_servico (ordem_servico_id, servico_id, valor_item_servico, status_servico, data_hora_inicio, data_criacao, data_ultima_atualizacao)
SELECT os.id, s.id, 150.00, 'AGUARDANDO_INICIO', NOW(), NOW(), NOW()
FROM ordem_servico os, servicos s
WHERE os.numero_ordem_servico = 1002 AND s.descricao = 'Troca de óleo';

-- Inserir item de serviço na OS EM_EXECUCAO (EXECUTANDO - para finalizar)
INSERT INTO os_item_servico (ordem_servico_id, servico_id, valor_item_servico, status_servico, data_hora_inicio, data_criacao, data_ultima_atualizacao)
SELECT os.id, s.id, 120.00, 'EXECUTANDO', NOW(), NOW(), NOW()
FROM ordem_servico os, servicos s
WHERE os.numero_ordem_servico = 1002 AND s.descricao = 'Alinhamento';

-- Inserir item de serviço na OS EM_DIAGNOSTICO (para remover)
INSERT INTO os_item_servico (ordem_servico_id, servico_id, valor_item_servico, status_servico, data_hora_inicio, data_criacao, data_ultima_atualizacao)
SELECT os.id, s.id, 150.00, 'AGUARDANDO_INICIO', NOW(), NOW(), NOW()
FROM ordem_servico os, servicos s
WHERE os.numero_ordem_servico = 1001 AND s.descricao = 'Troca de óleo';
