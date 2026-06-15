package com.avaliacao.dao;

import com.avaliacao.exception.InfraestruturaException;
import com.avaliacao.util.ConnectionFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JuncaoDAOImpl implements JuncaoDAO, Serializable {
    private static final long serialVersionUID = 1L;

    private Connection obterConexao() {
        try {
            return ConnectionFactory.getConnection();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao obter conexao com o banco", e);
        }
    }

    private void fecharRecursos(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
        try { if (conn != null) conn.close(); } catch (SQLException e) { }
    }

    // =========================================================
    // AVALIACAO <-> PROCESSO
    // =========================================================

    @Override
    public void inserirVinculoAvaliacaoProcesso(Long avaliacaoId, Long processoId) {
        String sql = "INSERT INTO avaliacao_processo (avaliacao_id, processo_id) VALUES (?, ?)";
        executarInsert(sql, avaliacaoId, processoId);
    }

    @Override
    public void removerVinculoAvaliacaoProcesso(Long avaliacaoId, Long processoId) {
        String sql = "DELETE FROM avaliacao_processo WHERE avaliacao_id = ? AND processo_id = ?";
        executarUpdate(sql, avaliacaoId, processoId);
    }

    @Override
    public List<Long> listarProcessosPorAvaliacao(Long avaliacaoId) {
        return listarIds("SELECT processo_id FROM avaliacao_processo WHERE avaliacao_id = ?", avaliacaoId);
    }

    @Override
    public List<Long> listarAvaliacoesPorProcesso(Long processoId) {
        return listarIds("SELECT avaliacao_id FROM avaliacao_processo WHERE processo_id = ?", processoId);
    }

    // =========================================================
    // PROCESSO <-> RISCO
    // =========================================================

    @Override
    public void inserirVinculoProcessoRisco(Long avaliacaoId, Long processoId, Long riscoId) {
        String sql = "INSERT INTO processo_risco (avaliacao_id, processo_id, risco_id) VALUES (?, ?, ?)";
        executarInsert(sql, avaliacaoId, processoId, riscoId);
    }

    @Override
    public void removerVinculoProcessoRisco(Long processoId, Long riscoId) {
        String sql = "DELETE FROM processo_risco WHERE processo_id = ? AND risco_id = ?";
        executarUpdate(sql, processoId, riscoId);
    }

    @Override
    public List<Long> listarRiscosPorProcesso(Long processoId) {
        return listarIds("SELECT risco_id FROM processo_risco WHERE processo_id = ?", processoId);
    }

    @Override
    public boolean possuiFilhosProcesso(Long processoId) {
        return existeVinculo("SELECT 1 FROM processo_risco WHERE processo_id = ? LIMIT 1", processoId);
    }

    // =========================================================
    // RISCO <-> FATOR
    // =========================================================

    @Override
    public void inserirVinculoRiscoFator(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId) {
        String sql = "INSERT INTO risco_fator (avaliacao_id, processo_id, risco_id, fator_id) VALUES (?, ?, ?, ?)";
        executarInsert(sql, avaliacaoId, processoId, riscoId, fatorId);
    }

    @Override
    public void removerVinculoRiscoFator(Long riscoId, Long fatorId) {
        String sql = "DELETE FROM risco_fator WHERE risco_id = ? AND fator_id = ?";
        executarUpdate(sql, riscoId, fatorId);
    }

    @Override
    public List<Long> listarFatoresPorRisco(Long riscoId) {
        return listarIds("SELECT fator_id FROM risco_fator WHERE risco_id = ?", riscoId);
    }

    @Override
    public boolean possuiFilhosRisco(Long riscoId) {
        return existeVinculo("SELECT 1 FROM risco_fator WHERE risco_id = ? LIMIT 1", riscoId);
    }

    // =========================================================
    // FATOR <-> CONTROLE
    // =========================================================

    @Override
    public void inserirVinculoFatorControle(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId) {
        String sql = "INSERT INTO fator_controle (avaliacao_id, processo_id, risco_id, fator_id, controle_id) VALUES (?, ?, ?, ?, ?)";
        executarInsert(sql, avaliacaoId, processoId, riscoId, fatorId, controleId);
    }

    @Override
    public void removerVinculoFatorControle(Long fatorId, Long controleId) {
        String sql = "DELETE FROM fator_controle WHERE fator_id = ? AND controle_id = ?";
        executarUpdate(sql, fatorId, controleId);
    }

    @Override
    public List<Long> listarControlesPorFator(Long fatorId) {
        return listarIds("SELECT controle_id FROM fator_controle WHERE fator_id = ?", fatorId);
    }

    @Override
    public boolean possuiFilhosFator(Long fatorId) {
        return existeVinculo("SELECT 1 FROM fator_controle WHERE fator_id = ? LIMIT 1", fatorId);
    }

    // =========================================================
    // CONTROLE <-> MODELO_NEGOCIO
    // =========================================================

    @Override
    public void inserirVinculoControleModeloNegocio(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long modeloNegocioId) {
        String sql = "INSERT INTO controle_modelo_negocio (avaliacao_id, processo_id, risco_id, fator_id, controle_id, modelo_negocio_id) VALUES (?, ?, ?, ?, ?, ?)";
        executarInsert(sql, avaliacaoId, processoId, riscoId, fatorId, controleId, modeloNegocioId);
    }

    @Override
    public void removerVinculoControleModeloNegocio(Long controleId, Long modeloNegocioId) {
        String sql = "DELETE FROM controle_modelo_negocio WHERE controle_id = ? AND modelo_negocio_id = ?";
        executarUpdate(sql, controleId, modeloNegocioId);
    }

    @Override
    public List<Long> listarModelosPorControle(Long controleId) {
        return listarIds("SELECT modelo_negocio_id FROM controle_modelo_negocio WHERE controle_id = ?", controleId);
    }

    // =========================================================
    // CONTROLE <-> TESTE (visao controle)
    // =========================================================

    @Override
    public void inserirVinculoControleTeste(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long testeId) {
        String sql = "INSERT INTO controle_teste (avaliacao_id, processo_id, risco_id, fator_id, controle_id, teste_id) VALUES (?, ?, ?, ?, ?, ?)";
        executarInsert(sql, avaliacaoId, processoId, riscoId, fatorId, controleId, testeId);
    }

    @Override
    public void removerVinculoControleTeste(Long controleId, Long testeId) {
        String sql = "DELETE FROM controle_teste WHERE controle_id = ? AND teste_id = ?";
        executarUpdate(sql, controleId, testeId);
    }

    @Override
    public List<Long> listarTestesPorControle(Long controleId) {
        return listarIds("SELECT teste_id FROM controle_teste WHERE controle_id = ?", controleId);
    }

    @Override
    public boolean possuiFilhosControle(Long controleId) {
        return existeVinculo("SELECT 1 FROM controle_teste WHERE controle_id = ? LIMIT 1", controleId);
    }

    // =========================================================
    // TESTE <-> CONTROLE (visao teste, N:M)
    // =========================================================

    @Override
    public void inserirVinculoTesteControle(Long testeId, Long controleId) {
        String sql = "INSERT INTO teste_controle (teste_id, controle_id) VALUES (?, ?)";
        executarInsert(sql, testeId, controleId);
    }

    @Override
    public void removerVinculoTesteControle(Long testeId, Long controleId) {
        String sql = "DELETE FROM teste_controle WHERE teste_id = ? AND controle_id = ?";
        executarUpdate(sql, testeId, controleId);
    }

    @Override
    public void limparVinculosTeste(Long testeId) {
        String sql = "DELETE FROM teste_controle WHERE teste_id = ?";
        executarUpdate(sql, testeId);
    }

    @Override
    public List<Long> listarControlesPorTeste(Long testeId) {
        return listarIds("SELECT controle_id FROM teste_controle WHERE teste_id = ?", testeId);
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    private void executarInsert(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao inserir vinculo", e);
        } finally {
            fecharRecursos(conn, stmt, null);
        }
    }

    private void executarUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao executar operacao no vinculo", e);
        } finally {
            fecharRecursos(conn, stmt, null);
        }
    }

    private List<Long> listarIds(String sql, Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            return ids;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar ids por vinculo", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private boolean existeVinculo(String sql, Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao verificar existencia de vinculo", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarRiscosPorProcessos(java.util.Set<Long> processoIds) {
        if (processoIds == null || processoIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(processoIds.size(), "?"));
        String sql = "SELECT processo_id, risco_id FROM processo_risco WHERE processo_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, processoIds);
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarFatoresPorRiscos(java.util.Set<Long> riscoIds) {
        if (riscoIds == null || riscoIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(riscoIds.size(), "?"));
        String sql = "SELECT risco_id, fator_id FROM risco_fator WHERE risco_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, riscoIds);
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarControlesPorFatores(java.util.Set<Long> fatorIds) {
        if (fatorIds == null || fatorIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(fatorIds.size(), "?"));
        String sql = "SELECT fator_id, controle_id FROM fator_controle WHERE fator_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, fatorIds);
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarTestesPorControles(java.util.Set<Long> controleIds) {
        if (controleIds == null || controleIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(controleIds.size(), "?"));
        String sql = "SELECT controle_id, teste_id FROM controle_teste WHERE controle_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, controleIds);
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarModelosPorControles(java.util.Set<Long> controleIds) {
        if (controleIds == null || controleIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(controleIds.size(), "?"));
        String sql = "SELECT controle_id, modelo_negocio_id FROM controle_modelo_negocio WHERE controle_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, controleIds);
    }

    @Override
    public java.util.Map<Long, java.util.List<Long>> listarControlesPorTestes(java.util.Set<Long> testeIds) {
        if (testeIds == null || testeIds.isEmpty()) {
            return new java.util.HashMap<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(testeIds.size(), "?"));
        String sql = "SELECT teste_id, controle_id FROM teste_controle WHERE teste_id IN (" + placeholders + ")";
        return executarBatchListarIds(sql, testeIds);
    }

    private java.util.Map<Long, java.util.List<Long>> executarBatchListarIds(String sql, java.util.Set<Long> ids) {
        java.util.Map<Long, java.util.List<Long>> resultado = new java.util.HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            int i = 1;
            for (Long id : ids) {
                stmt.setLong(i++, id);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long parentId = rs.getLong(1);
                Long childId = rs.getLong(2);
                resultado.computeIfAbsent(parentId, k -> new java.util.ArrayList<>()).add(childId);
            }
            return resultado;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao executar batch listar ids", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }
}
