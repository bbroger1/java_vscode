package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Controle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ControleDAOImpl extends GenericDAOImpl<Controle> implements ControleDAO {

    @Override
    protected Controle mapearLinha(ResultSet rs) throws SQLException {
        Controle c = new Controle();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        c.setDescricao(rs.getString("descricao"));
        c.setTipo(rs.getString("tipo"));
        c.setStatus(rs.getString("status"));
        if (rs.getTimestamp("data_criacao") != null) {
            c.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return c;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, tipo, status, data_criacao FROM controle WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, tipo, status, data_criacao FROM controle ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO controle (nome, descricao, tipo, status, data_criacao) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE controle SET nome = ?, descricao = ?, tipo = ?, status = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM controle WHERE id = ?";

    @Override
    public Controle buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Controle> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Controle c) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, c.getNome(), c.getDescricao(), c.getTipo(), c.getStatus(), agora);
    }

    @Override
    public void atualizar(Controle c) {
        atualizar(SQL_ATUALIZAR, c.getNome(), c.getDescricao(), c.getTipo(), c.getStatus(), c.getId());
    }

    @Override
    public void excluir(Long id) {
        excluir(SQL_EXCLUIR, id);
    }
}
