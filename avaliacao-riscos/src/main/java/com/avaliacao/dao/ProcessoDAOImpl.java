package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Processo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProcessoDAOImpl extends GenericDAOImpl<Processo> implements ProcessoDAO {

    @Override
    protected Processo mapearLinha(ResultSet rs) throws SQLException {
        Processo p = new Processo();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setCodigo(rs.getString("codigo"));
        if (rs.getTimestamp("data_criacao") != null) {
            p.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return p;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, codigo, data_criacao FROM processo WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, codigo, data_criacao FROM processo ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO processo (nome, descricao, codigo, data_criacao) VALUES (?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE processo SET nome = ?, descricao = ?, codigo = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM processo WHERE id = ?";

    @Override
    public Processo buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Processo> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Processo p) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, p.getNome(), p.getDescricao(), p.getCodigo(), agora);
    }

    @Override
    public void atualizar(Processo p) {
        atualizar(SQL_ATUALIZAR, p.getNome(), p.getDescricao(), p.getCodigo(), p.getId());
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
