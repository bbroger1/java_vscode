package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Fator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FatorDAOImpl extends GenericDAOImpl<Fator> implements FatorDAO {

    @Override
    protected Fator mapearLinha(ResultSet rs) throws SQLException {
        Fator f = new Fator();
        f.setId(rs.getLong("id"));
        f.setNome(rs.getString("nome"));
        f.setDescricao(rs.getString("descricao"));
        f.setTipo(rs.getString("tipo"));
        if (rs.getTimestamp("data_criacao") != null) {
            f.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return f;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, tipo, data_criacao FROM fator WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, tipo, data_criacao FROM fator ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO fator (nome, descricao, tipo, data_criacao) VALUES (?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE fator SET nome = ?, descricao = ?, tipo = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM fator WHERE id = ?";

    @Override
    public Fator buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Fator> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Fator f) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, f.getNome(), f.getDescricao(), f.getTipo(), agora);
    }

    @Override
    public void atualizar(Fator f) {
        atualizar(SQL_ATUALIZAR, f.getNome(), f.getDescricao(), f.getTipo(), f.getId());
    }

    @Override
    public void excluir(Long id) {
        excluir(SQL_EXCLUIR, id);
    }
}
