package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.ModeloNegocio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ModeloNegocioDAOImpl extends GenericDAOImpl<ModeloNegocio> implements ModeloNegocioDAO {

    @Override
    protected ModeloNegocio mapearLinha(ResultSet rs) throws SQLException {
        ModeloNegocio m = new ModeloNegocio();
        m.setId(rs.getLong("id"));
        m.setNome(rs.getString("nome"));
        m.setDescricao(rs.getString("descricao"));
        m.setVersao(rs.getString("versao"));
        if (rs.getTimestamp("data_criacao") != null) {
            m.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        return m;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, nome, descricao, versao, data_criacao FROM modelo_negocio WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, nome, descricao, versao, data_criacao FROM modelo_negocio ORDER BY nome";

    private static final String SQL_INSERIR =
        "INSERT INTO modelo_negocio (nome, descricao, versao, data_criacao) VALUES (?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE modelo_negocio SET nome = ?, descricao = ?, versao = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM modelo_negocio WHERE id = ?";

    @Override
    public ModeloNegocio buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<ModeloNegocio> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(ModeloNegocio m) {
        java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, m.getNome(), m.getDescricao(), m.getVersao(), agora);
    }

    @Override
    public void atualizar(ModeloNegocio m) {
        atualizar(SQL_ATUALIZAR, m.getNome(), m.getDescricao(), m.getVersao(), m.getId());
    }

    @Override
    public void excluir(Long id) {
        excluir(SQL_EXCLUIR, id);
    }
}
