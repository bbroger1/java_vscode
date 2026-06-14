package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Teste;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TesteDAOImpl extends GenericDAOImpl<Teste> implements TesteDAO {

    @Override
    protected Teste mapearLinha(ResultSet rs) throws SQLException {
        Teste t = new Teste();
        t.setId(rs.getLong("id"));
        t.setNome(rs.getString("nome"));
        t.setDescricao(rs.getString("descricao"));
        t.setTipo(rs.getString("tipo"));
        t.setResultado(rs.getString("resultado"));
        if (rs.getDate("data_execucao") != null) {
            t.setDataExecucao(rs.getDate("data_execucao").toLocalDate());
        }
        if (rs.getTimestamp("data_criacao") != null) {
            t.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return t;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, tipo, resultado, data_execucao, data_criacao FROM teste WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, tipo, resultado, data_execucao, data_criacao FROM teste ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO teste (nome, descricao, tipo, resultado, data_execucao, data_criacao) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE teste SET nome = ?, descricao = ?, tipo = ?, resultado = ?, data_execucao = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM teste WHERE id = ?";

    @Override
    public Teste buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Teste> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Teste t) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        Date dataExec = (t.getDataExecucao() != null) ? Date.valueOf(t.getDataExecucao()) : null;
        return inserir(SQL_INSERIR, t.getNome(), t.getDescricao(),
            t.getTipo(), t.getResultado(), dataExec, agora);
    }

    @Override
    public void atualizar(Teste t) {
        Date dataExec = (t.getDataExecucao() != null) ? Date.valueOf(t.getDataExecucao()) : null;
        atualizar(SQL_ATUALIZAR, t.getNome(), t.getDescricao(), t.getTipo(),
            t.getResultado(), dataExec, t.getId());
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
