-- ========================================
-- Migration: V7__inserir_ordens_servico
-- Descrição: Insere ordens de serviço de exemplo para testes
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================

-- OS 1: João da Silva (Cliente 1) com o Toyota Corolla (Veiculo 1)
INSERT INTO ordem_servico (
    numero_ordem_servico, status, valor_total, veiculo_id, cliente_id,
    data_criacao, data_ultima_atualizacao
) VALUES (
    1001, 'RECEBIDA', 1500.50, 1, 1, NOW(), NOW()
);

-- OS 2: Maria Santos (Cliente 2) com o Honda Civic (Veiculo 2)
INSERT INTO ordem_servico (
    numero_ordem_servico, status, valor_total, veiculo_id, cliente_id,
    data_criacao, data_ultima_atualizacao
) VALUES (
    1002, 'EM_EXECUCAO', 2300.00, 2, 2, NOW(), NOW()
);

-- OS 3: Tech Solutions LTDA (Cliente 4) com a Ford Ranger (Veiculo 3)
INSERT INTO ordem_servico (
    numero_ordem_servico, status, valor_total, veiculo_id, cliente_id,
    data_criacao, data_ultima_atualizacao
) VALUES (
    1003, 'FINALIZADA', 850.75, 3, 4, NOW(), NOW()
);
