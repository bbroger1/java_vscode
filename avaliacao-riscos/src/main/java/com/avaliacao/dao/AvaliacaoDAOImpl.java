package com.avaliacao.dao;

import com.avaliacao.dao.generic.GenericDAOImpl;
import com.avaliacao.model.Avaliacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AvaliacaoDAOImpl extends GenericDAOImpl<Avaliacao> implements AvaliacaoDAO {

    @Override
    protected Avaliacao mapearLinha(ResultSet rs) throws SQLException {
        Avaliacao a = new Avaliacao();
        a.setId(rs.getLong("id"));
        a.setTitulo(rs.getString("titulo"));
        a.setDescricao(rs.getString("descricao"));
        a.setStatus(rs.getString("status"));
        if (rs.getTimestamp("data_criacao") != null) {
            a.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        if (rs.getTimestamp("data_atualizacao") != null) {
            a.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        }
        return a;
    }

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id, titulo, descricao, status, data_criacao, data_atualizacao FROM avaliacao WHERE id = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id, titulo, descricao, status, data_criacao, data_atualizacao FROM avaliacao ORDER BY data_criacao DESC";

    private static final String SQL_INSERIR =
        "INSERT INTO avaliacao (titulo, descricao, status, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
        "UPDATE avaliacao SET titulo = ?, descricao = ?, status = ?, data_atualizacao = ? WHERE id = ?";

    private static final String SQL_EXCLUIR =
        "DELETE FROM avaliacao WHERE id = ?";

    private static final String SQL_PESQUISAR =
        "SELECT id, titulo, descricao, status, data_criacao, data_atualizacao FROM avaliacao " +
        "WHERE LOWER(titulo) LIKE LOWER(?) OR LOWER(descricao) LIKE LOWER(?) " +
        "ORDER BY data_criacao DESC LIMIT ? OFFSET ?";

    private static final String SQL_CONTAR =
        "SELECT COUNT(*) FROM avaliacao WHERE LOWER(titulo) LIKE LOWER(?) OR LOWER(descricao) LIKE LOWER(?)";

    @Override
    public Avaliacao buscarPorId(Long id) {
        return buscarPorId(SQL_BUSCAR_POR_ID, id);
    }

    @Override
    public List<Avaliacao> listarTodos() {
        return listarTodos(SQL_LISTAR_TODOS);
    }

    @Override
    public Long inserir(Avaliacao a) {
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        return inserir(SQL_INSERIR, a.getTitulo(), a.getDescricao(), a.getStatus(), agora, agora);
    }

    @Override
    public void atualizar(Avaliacao a) {
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        atualizar(SQL_ATUALIZAR, a.getTitulo(), a.getDescricao(), a.getStatus(), agora, a.getId());
    }

    @Override
    public void excluir(Long id) {
        excluir(SQL_EXCLUIR, id);
    }

    @Override
    public List<Avaliacao> pesquisar(String filtro, int offset, int limit) {
        return pesquisar(SQL_PESQUISAR, offset, limit, filtro, filtro);
    }

    @Override
    public int contar(String filtro) {
        return contar(SQL_CONTAR, filtro, filtro);
    }
}
