package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Risco;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RiscoDAOImpl extends GenericDAOImpl<Risco> implements RiscoDAO {

    @Override
    protected Risco mapearLinha(ResultSet rs) throws SQLException {
        Risco r = new Risco();
        r.setId(rs.getLong("id"));
        r.setNome(rs.getString("nome"));
        r.setDescricao(rs.getString("descricao"));
        r.setProbabilidade(rs.getString("probabilidade"));
        r.setImpacto(rs.getString("impacto"));
        r.setNivel(rs.getString("nivel"));
        if (rs.getTimestamp("data_criacao") != null) {
            r.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return r;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, probabilidade, impacto, nivel, data_criacao FROM risco WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, probabilidade, impacto, nivel, data_criacao FROM risco ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO risco (nome, descricao, probabilidade, impacto, nivel, data_criacao) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE risco SET nome = ?, descricao = ?, probabilidade = ?, impacto = ?, nivel = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM risco WHERE id = ?";

    @Override
    public Risco buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Risco> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Risco r) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, r.getNome(), r.getDescricao(),
            r.getProbabilidade(), r.getImpacto(), r.getNivel(), agora);
    }

    @Override
    public void atualizar(Risco r) {
        atualizar(SQL_ATUALIZAR, r.getNome(), r.getDescricao(),
            r.getProbabilidade(), r.getImpacto(), r.getNivel(), r.getId());
    }

    @Override
    public void excluir(Long id) {
        excluir(SQL_EXCLUIR, id);
    }

    @Override
    protected String getSqlListarTodos() {
        return SQL_LISTAR_TODOS;
    }
}
