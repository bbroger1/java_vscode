-- ============================================================
-- DADOS FAKES - Sistema de Avaliacao de Riscos e Controles
-- Para testes da hierarquia completa com tabelas de juncao
-- ============================================================
-- ATENCAO: Executar APOS o DDL (ddl-avaliacao-riscos.sql)
-- ============================================================

BEGIN;

-- ============================================================
-- ENTIDADES PRINCIPAIS
-- ============================================================

-- AVALIACAO
INSERT INTO avaliacao (id, titulo, descricao, status) VALUES
(nextval('seq_avaliacao'), 'Avaliacao de Riscos - Sistema Financeiro',
 'Avaliacao abrangente dos riscos do sistema financeiro corporativo, incluindo creditio, operacoes e compliance.',
 'EM_ANDAMENTO');

-- MODELO_NEGOCIO
INSERT INTO modelo_negocio (id, nome, descricao, versao) VALUES
(nextval('seq_modelo_negocio'), 'Modelo de Negocio - Cartoes',
 'Modelo de negocio para operacoes com cartoes de credito e debito', '1.0'),
(nextval('seq_modelo_negocio'), 'Modelo de Negocio - Emprestimos',
 'Modelo de negocio para operacoes de emprestimos e financiamentos', '1.2');

-- PROCESSO
INSERT INTO processo (id, nome, descricao, codigo) VALUES
(nextval('seq_processo'), 'Processo de Concessao de Credito',
 'Processo de analise e aprovacao de creditio para pessoas fisicas e juridicas', 'PCR-001'),
(nextval('seq_processo'), 'Processo de Transacao Financeira',
 'Processo de processamento de transacoes financeiras em tempo real', 'PTF-002'),
(nextval('seq_processo'), 'Processo de Compliance',
 'Processo de conformidade regulatoria e prevencao a lavagem de dinheiro', 'PCP-003');

-- RISCO
INSERT INTO risco (id, nome, descricao, probabilidade, impacto, nivel) VALUES
(nextval('seq_risco'), 'Risco de Credito',
 'Risco de inadimplencia dos tomadores de creditio', 'MEDIA', 'ALTO', 'ALTO'),
(nextval('seq_risco'), 'Risco Operacional',
 'Risco de falhas em processos internos, sistemas ou erro humano', 'ALTA', 'MEDIO', 'ALTO'),
(nextval('seq_risco'), 'Risco de Liquidez',
 'Risco de incapacidade de cumprir obrigacoes financeiras', 'BAIXA', 'ALTO', 'MEDIO'),
(nextval('seq_risco'), 'Risco de Fraude',
 'Risco de fraudes internas ou externas envolvendo o sistema', 'MEDIA', 'CRITICO', 'CRITICO');

-- FATOR
INSERT INTO fator (id, nome, descricao, tipo) VALUES
(nextval('seq_fator'), 'Fator Economico',
 'Condicoes economicas e de mercado que afetam o negocio', 'EXTERNO'),
(nextval('seq_fator'), 'Fator Tecnologico',
 'Infraestrutura tecnologica e vulnerabilidades de sistemas', 'TECNOLOGICO'),
(nextval('seq_fator'), 'Fator Humano',
 'Erros humanos, capacitacao e conscientizacao dos colaboradores', 'HUMANO');

-- CONTROLE
INSERT INTO controle (id, nome, descricao, tipo, status) VALUES
(nextval('seq_controle'), 'Controle de Acesso',
 'Sistema de autenticacao e autorizacao com 2 fatores e RBAC', 'PREVENTIVO', 'ATIVO'),
(nextval('seq_controle'), 'Monitoramento de Transacoes',
 'Sistema de monitoramento em tempo real de transacoes suspeitas', 'DETECTIVO', 'ATIVO'),
(nextval('seq_controle'), 'Auditoria Interna',
 'Processo de auditoria interna trimestral dos processos criticos', 'DETECTIVO', 'ATIVO'),
(nextval('seq_controle'), 'Backup e Recuperacao',
 'Politica de backup diario e plano de recuperacao de desastres', 'CORRETIVO', 'ATIVO');

-- TESTE
INSERT INTO teste (id, nome, descricao, tipo, resultado, data_execucao) VALUES
(nextval('seq_teste'), 'Teste de Penetracao',
 'Teste de penetracao externo e interno nos sistemas criticos', 'MANUAL', 'APROVADO', '2026-05-15'),
(nextval('seq_teste'), 'Teste de Estresse',
 'Teste de estresse sob carga maxima de transacoes simultaneas', 'AUTOMATIZADO', 'APROVADO', '2026-05-20'),
(nextval('seq_teste'), 'Teste de Conformidade',
 'Teste de conformidade com normas regulatorias (BACEN, LGPD)', 'REVISAO', 'EM_ANDAMENTO', NULL);

-- ============================================================
-- TABELAS DE JUNCAO (hierarquia completa de ancestrais)
-- ============================================================

-- Variaveis de controle (IDs)
-- Avaliacao ID = 1 (primeira sequencia)
-- Processos ID = 1, 2, 3
-- Riscos ID = 1, 2, 3, 4
-- Fatores ID = 1, 2, 3
-- Controles ID = 1, 2, 3, 4
-- Testes ID = 1, 2, 3
-- ModeloNegocio ID = 1, 2

DO $$
DECLARE
    v_avaliacao_id BIGINT := 1;
    v_p1 BIGINT := 1; -- Processo Concessao Credito
    v_p2 BIGINT := 2; -- Processo Transacao Financeira
    v_p3 BIGINT := 3; -- Processo Compliance
    v_r1 BIGINT := 1; -- Risco de Credito
    v_r2 BIGINT := 2; -- Risco Operacional
    v_r3 BIGINT := 3; -- Risco de Liquidez
    v_r4 BIGINT := 4; -- Risco de Fraude
    v_f1 BIGINT := 1; -- Fator Economico
    v_f2 BIGINT := 2; -- Fator Tecnologico
    v_f3 BIGINT := 3; -- Fator Humano
    v_c1 BIGINT := 1; -- Controle de Acesso
    v_c2 BIGINT := 2; -- Monitoramento Transacoes
    v_c3 BIGINT := 3; -- Auditoria Interna
    v_c4 BIGINT := 4; -- Backup e Recuperacao
    v_t1 BIGINT := 1; -- Teste Penetracao
    v_t2 BIGINT := 2; -- Teste Estresse
    v_t3 BIGINT := 3; -- Teste Conformidade
    v_m1 BIGINT := 1; -- Modelo Cartoes
    v_m2 BIGINT := 2; -- Modelo Emprestimos
BEGIN

    -- ============================================================
    -- NIVEL 1: Avaliacao <-> Processo
    -- ============================================================
    -- Avaliacao 1 -> Processos 1, 2, 3
    INSERT INTO avaliacao_processo (avaliacao_id, processo_id)
    VALUES (v_avaliacao_id, v_p1),
           (v_avaliacao_id, v_p2),
           (v_avaliacao_id, v_p3);

    -- ============================================================
    -- NIVEL 2: Avaliacao -> Processo <-> Risco
    -- ============================================================
    -- Processo 1 (Concessao Credito) -> Risco 1 (Credito), Risco 2 (Operacional)
    INSERT INTO processo_risco (avaliacao_id, processo_id, risco_id)
    VALUES (v_avaliacao_id, v_p1, v_r1),
           (v_avaliacao_id, v_p1, v_r2);

    -- Processo 2 (Transacao Financeira) -> Risco 3 (Liquidez)
    INSERT INTO processo_risco (avaliacao_id, processo_id, risco_id)
    VALUES (v_avaliacao_id, v_p2, v_r3);

    -- Processo 3 (Compliance) -> Risco 4 (Fraude)
    INSERT INTO processo_risco (avaliacao_id, processo_id, risco_id)
    VALUES (v_avaliacao_id, v_p3, v_r4);

    -- ============================================================
    -- NIVEL 3: Avaliacao -> Processo -> Risco <-> Fator
    -- ============================================================
    -- Risco 1 (Credito) -> Fator 1 (Economico), Fator 2 (Tecnologico)
    INSERT INTO risco_fator (avaliacao_id, processo_id, risco_id, fator_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f1),
           (v_avaliacao_id, v_p1, v_r1, v_f2);

    -- Risco 2 (Operacional) -> Fator 3 (Humano)
    INSERT INTO risco_fator (avaliacao_id, processo_id, risco_id, fator_id)
    VALUES (v_avaliacao_id, v_p1, v_r2, v_f3);

    -- Risco 3 (Liquidez) -> Fator 1 (Economico)
    INSERT INTO risco_fator (avaliacao_id, processo_id, risco_id, fator_id)
    VALUES (v_avaliacao_id, v_p2, v_r3, v_f1);

    -- Risco 4 (Fraude) -> Fator 2 (Tecnologico), Fator 3 (Humano)
    INSERT INTO risco_fator (avaliacao_id, processo_id, risco_id, fator_id)
    VALUES (v_avaliacao_id, v_p3, v_r4, v_f2),
           (v_avaliacao_id, v_p3, v_r4, v_f3);

    -- ============================================================
    -- NIVEL 4: Avaliacao -> ... -> Fator <-> Controle
    -- ============================================================
    -- Fator 1 (Economico) -> Controle 2 (Monitoramento Transacoes)
    INSERT INTO fator_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f1, v_c2);

    -- Fator 2 (Tecnologico) -> Controle 1 (Acesso), Controle 3 (Auditoria)
    INSERT INTO fator_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f2, v_c1),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3);

    -- Fator 3 (Humano) -> Controle 4 (Backup e Recuperacao)
    INSERT INTO fator_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id)
    VALUES (v_avaliacao_id, v_p1, v_r2, v_f3, v_c4);

    -- Tambem para risco 4 -> fator 2 -> controle
    INSERT INTO fator_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id)
    VALUES (v_avaliacao_id, v_p3, v_r4, v_f2, v_c1),
           (v_avaliacao_id, v_p3, v_r4, v_f3, v_c4);

    -- ============================================================
    -- NIVEL 5: Avaliacao -> ... -> Controle <-> ModeloNegocio
    -- ============================================================
    INSERT INTO controle_modelo_negocio (avaliacao_id, processo_id, risco_id, fator_id, controle_id, modelo_negocio_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f2, v_c1, v_m1),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c1, v_m2),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3, v_m1);

    -- ============================================================
    -- NIVEL 6: Controle <-> Teste (visao do controle)
    -- ============================================================
    INSERT INTO controle_teste (avaliacao_id, processo_id, risco_id, fator_id, controle_id, teste_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f2, v_c1, v_t1),
           (v_avaliacao_id, v_p1, v_r1, v_f1, v_c2, v_t2),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3, v_t1),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3, v_t3);

    -- ============================================================
    -- NIVEL 7: Teste <-> Controle (visao do teste, N:M)
    -- ============================================================
    INSERT INTO teste_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id, teste_id)
    VALUES (v_avaliacao_id, v_p1, v_r1, v_f2, v_c1, v_t1),
           (v_avaliacao_id, v_p1, v_r1, v_f1, v_c2, v_t2),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3, v_t1),
           (v_avaliacao_id, v_p1, v_r1, v_f2, v_c3, v_t3);

    RAISE NOTICE 'Dados fakes inseridos com sucesso!';
END $$;

COMMIT;
