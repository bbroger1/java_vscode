-- ============================================================
-- SCRIPT DDL - Sistema de Avaliacao de Riscos e Controles
-- PostgreSQL 14+
-- Refatorado: entidades independentes com tabelas de juncao
-- ============================================================

-- SEQUENCES
CREATE SEQUENCE IF NOT EXISTS seq_avaliacao START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_processo START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_risco START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_fator START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_controle START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_teste START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_modelo_negocio START 1 INCREMENT 1;

-- ============================================================
-- TABELA: avaliacao
-- ============================================================
CREATE TABLE IF NOT EXISTS avaliacao (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_avaliacao'),
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE avaliacao IS 'Armazena as avaliacoes de riscos';
COMMENT ON COLUMN avaliacao.status IS 'EM_ANDAMENTO | CONCLUIDA | ARQUIVADA';
CREATE INDEX IF NOT EXISTS idx_avaliacao_titulo ON avaliacao USING btree (titulo);
CREATE INDEX IF NOT EXISTS idx_avaliacao_status ON avaliacao USING btree (status);

-- ============================================================
-- TABELA: modelo_negocio
-- ============================================================
CREATE TABLE IF NOT EXISTS modelo_negocio (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_modelo_negocio'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    versao VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE modelo_negocio IS 'Modelos de negocio que podem ser vinculados a controles';
CREATE INDEX IF NOT EXISTS idx_modelo_negocio_nome ON modelo_negocio USING btree (nome);

-- ============================================================
-- TABELA: processo (independente, sem FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS processo (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_processo'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    codigo VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE processo IS 'Processos (independentes, vinculados via tabela de juncao)';
COMMENT ON COLUMN processo.codigo IS 'Codigo identificador do processo';
CREATE INDEX IF NOT EXISTS idx_processo_nome ON processo USING btree (nome);

-- ============================================================
-- TABELA: risco (independente, sem FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS risco (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_risco'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    probabilidade VARCHAR(20),
    impacto VARCHAR(20),
    nivel VARCHAR(20),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE risco IS 'Riscos (independentes, vinculados via tabela de juncao)';
COMMENT ON COLUMN risco.probabilidade IS 'BAIXA | MEDIA | ALTA';
COMMENT ON COLUMN risco.impacto IS 'BAIXO | MEDIO | ALTO';
COMMENT ON COLUMN risco.nivel IS 'BAIXO | MEDIO | ALTO | CRITICO';
CREATE INDEX IF NOT EXISTS idx_risco_nome ON risco USING btree (nome);
CREATE INDEX IF NOT EXISTS idx_risco_nivel ON risco USING btree (nivel);

-- ============================================================
-- TABELA: fator (independente, sem FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS fator (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_fator'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE fator IS 'Fatores (independentes, vinculados via tabela de juncao)';
COMMENT ON COLUMN fator.tipo IS 'INTERNO | EXTERNO | TECNOLOGICO | HUMANO';
CREATE INDEX IF NOT EXISTS idx_fator_nome ON fator USING btree (nome);

-- ============================================================
-- TABELA: controle (independente, sem FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS controle (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_controle'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE controle IS 'Controles (independentes, vinculados via tabela de juncao)';
COMMENT ON COLUMN controle.tipo IS 'PREVENTIVO | DETECTIVO | CORRETIVO';
COMMENT ON COLUMN controle.status IS 'ATIVO | INATIVO | EM_IMPLANTACAO';
CREATE INDEX IF NOT EXISTS idx_controle_nome ON controle USING btree (nome);
CREATE INDEX IF NOT EXISTS idx_controle_status ON controle USING btree (status);

-- ============================================================
-- TABELA: teste (independente, sem FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS teste (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_teste'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50),
    resultado VARCHAR(20),
    data_execucao DATE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE teste IS 'Testes (independentes, vinculados via tabela de juncao)';
COMMENT ON COLUMN teste.tipo IS 'AUTOMATIZADO | MANUAL | REVISAO';
COMMENT ON COLUMN teste.resultado IS 'APROVADO | REPROVADO | NAO_EXECUTADO | EM_ANDAMENTO';
CREATE INDEX IF NOT EXISTS idx_teste_nome ON teste USING btree (nome);
CREATE INDEX IF NOT EXISTS idx_teste_resultado ON teste USING btree (resultado);

-- ============================================================
-- TABELAS DE JUNCAO (com hierarquia completa de ancestrais)
-- ============================================================

-- 1 NIVEL: Avaliacao <-> Processo
CREATE TABLE IF NOT EXISTS avaliacao_processo (
    avaliacao_id BIGINT NOT NULL,
    processo_id  BIGINT NOT NULL,
    PRIMARY KEY (avaliacao_id, processo_id),
    CONSTRAINT fk_ap_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_ap_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE
);
COMMENT ON TABLE avaliacao_processo IS 'Vincula processos a avaliacoes';

-- 2 NIVEL: Avaliacao -> Processo <-> Risco
CREATE TABLE IF NOT EXISTS processo_risco (
    avaliacao_id BIGINT NOT NULL,
    processo_id  BIGINT NOT NULL,
    risco_id     BIGINT NOT NULL,
    PRIMARY KEY (processo_id, risco_id),
    CONSTRAINT fk_pr_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_pr_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_pr_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_pr_avaliacao ON processo_risco(avaliacao_id);
COMMENT ON TABLE processo_risco IS 'Vincula riscos a processos dentro de uma avaliacao';

-- 3 NIVEL: Avaliacao -> Processo -> Risco <-> Fator
CREATE TABLE IF NOT EXISTS risco_fator (
    avaliacao_id BIGINT NOT NULL,
    processo_id  BIGINT NOT NULL,
    risco_id     BIGINT NOT NULL,
    fator_id     BIGINT NOT NULL,
    PRIMARY KEY (risco_id, fator_id),
    CONSTRAINT fk_rf_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_rf_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_rf_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE,
    CONSTRAINT fk_rf_fator FOREIGN KEY (fator_id)
        REFERENCES fator(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_rf_avaliacao ON risco_fator(avaliacao_id);
CREATE INDEX IF NOT EXISTS idx_rf_processo ON risco_fator(processo_id);
COMMENT ON TABLE risco_fator IS 'Vincula fatores a riscos dentro de uma avaliacao';

-- 4 NIVEL: Avaliacao -> Processo -> Risco -> Fator <-> Controle
CREATE TABLE IF NOT EXISTS fator_controle (
    avaliacao_id BIGINT NOT NULL,
    processo_id  BIGINT NOT NULL,
    risco_id     BIGINT NOT NULL,
    fator_id     BIGINT NOT NULL,
    controle_id  BIGINT NOT NULL,
    PRIMARY KEY (fator_id, controle_id),
    CONSTRAINT fk_fc_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_fc_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_fc_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE,
    CONSTRAINT fk_fc_fator FOREIGN KEY (fator_id)
        REFERENCES fator(id) ON DELETE CASCADE,
    CONSTRAINT fk_fc_controle FOREIGN KEY (controle_id)
        REFERENCES controle(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_fc_avaliacao ON fator_controle(avaliacao_id);
CREATE INDEX IF NOT EXISTS idx_fc_processo ON fator_controle(processo_id);
CREATE INDEX IF NOT EXISTS idx_fc_risco ON fator_controle(risco_id);
COMMENT ON TABLE fator_controle IS 'Vincula controles a fatores dentro de uma avaliacao';

-- 5 NIVEL: Avaliacao -> ... -> Controle <-> ModeloNegocio
CREATE TABLE IF NOT EXISTS controle_modelo_negocio (
    avaliacao_id      BIGINT NOT NULL,
    processo_id       BIGINT NOT NULL,
    risco_id          BIGINT NOT NULL,
    fator_id          BIGINT NOT NULL,
    controle_id       BIGINT NOT NULL,
    modelo_negocio_id BIGINT NOT NULL,
    PRIMARY KEY (controle_id, modelo_negocio_id),
    CONSTRAINT fk_cmn_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmn_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmn_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmn_fator FOREIGN KEY (fator_id)
        REFERENCES fator(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmn_controle FOREIGN KEY (controle_id)
        REFERENCES controle(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmn_modelo_negocio FOREIGN KEY (modelo_negocio_id)
        REFERENCES modelo_negocio(id) ON DELETE CASCADE
);
COMMENT ON TABLE controle_modelo_negocio IS 'Vincula modelos de negocio a controles';

-- 6 NIVEL: Avaliacao -> ... -> Controle <-> Teste (visao do controle)
CREATE TABLE IF NOT EXISTS controle_teste (
    avaliacao_id      BIGINT NOT NULL,
    processo_id       BIGINT NOT NULL,
    risco_id          BIGINT NOT NULL,
    fator_id          BIGINT NOT NULL,
    controle_id       BIGINT NOT NULL,
    modelo_negocio_id BIGINT,
    teste_id          BIGINT NOT NULL,
    PRIMARY KEY (controle_id, teste_id),
    CONSTRAINT fk_ct_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_ct_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_ct_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE,
    CONSTRAINT fk_ct_fator FOREIGN KEY (fator_id)
        REFERENCES fator(id) ON DELETE CASCADE,
    CONSTRAINT fk_ct_controle FOREIGN KEY (controle_id)
        REFERENCES controle(id) ON DELETE CASCADE,
    CONSTRAINT fk_ct_teste FOREIGN KEY (teste_id)
        REFERENCES teste(id) ON DELETE CASCADE
);
COMMENT ON TABLE controle_teste IS 'Vincula testes a controles (visao do controle)';

-- 7 NIVEL: Avaliacao -> ... -> Teste <-> Controle (visao do teste, N:M)
CREATE TABLE IF NOT EXISTS teste_controle (
    avaliacao_id      BIGINT NOT NULL,
    processo_id       BIGINT NOT NULL,
    risco_id          BIGINT NOT NULL,
    fator_id          BIGINT NOT NULL,
    controle_id       BIGINT NOT NULL,
    modelo_negocio_id BIGINT,
    teste_id          BIGINT NOT NULL,
    PRIMARY KEY (teste_id, controle_id),
    CONSTRAINT fk_tc_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_processo FOREIGN KEY (processo_id)
        REFERENCES processo(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_risco FOREIGN KEY (risco_id)
        REFERENCES risco(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_fator FOREIGN KEY (fator_id)
        REFERENCES fator(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_controle FOREIGN KEY (controle_id)
        REFERENCES controle(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_teste FOREIGN KEY (teste_id)
        REFERENCES teste(id) ON DELETE CASCADE
);
COMMENT ON TABLE teste_controle IS 'Vincula controles a testes (visao do teste, N:M)';
