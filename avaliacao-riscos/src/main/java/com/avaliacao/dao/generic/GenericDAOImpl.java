package com.avaliacao.dao.generic;

import com.avaliacao.exception.InfraestruturaException;
import com.avaliacao.util.ConnectionFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.avaliacao.util.DebugLog;

public abstract class GenericDAOImpl<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected abstract T mapearLinha(ResultSet rs) throws SQLException;

    protected Connection obterConexao() {
        try {
            return ConnectionFactory.getConnection();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao obter conexao com o banco", e);
        }
    }

    protected void fecharRecursos(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            // Log apenas, sem propagar
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            // Log apenas
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            // Log apenas
        }
    }

    protected T buscarPorId(String sql, Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearLinha(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar registro por ID", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    protected List<T> listarTodos(String sql) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            List<T> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
            DebugLog.log("listarTodos - Total de registros: " + lista.size());
            return lista;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao listar registros", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    protected Long inserir(String sql, Object... parametros) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao inserir registro", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    protected void atualizar(String sql, Object... parametros) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao atualizar registro", e);
        } finally {
            fecharRecursos(conn, stmt, null);
        }
    }

    protected void excluir(String sql, Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao excluir registro", e);
        } finally {
            fecharRecursos(conn, stmt, null);
        }
    }

    protected List<T> pesquisar(String sql, int offset, int limit, String... filtros) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            int i = 0;
            for (String f : filtros) {
                stmt.setString(++i, "%" + f + "%");
            }
            stmt.setInt(++i, limit);
            stmt.setInt(++i, offset);
            rs = stmt.executeQuery();
            List<T> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao pesquisar registros", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    protected int contar(String sql, String... filtros) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            
            // Tratar filtros nulos ou vazios
            String[] filtrosValidos = new String[filtros.length];
            for (int i = 0; i < filtros.length; i++) {
                if (filtros[i] == null || filtros[i].trim().isEmpty()) {
                    filtrosValidos[i] = "";
                } else {
                    filtrosValidos[i] = filtros[i];
                }
            }
            
            for (int i = 0; i < filtrosValidos.length; i++) {
                stmt.setString(i + 1, "%" + filtrosValidos[i] + "%");
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao contar registros", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public List<T> buscarPorIds(java.util.Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = getSqlListarTodos().replace("ORDER BY", "WHERE id IN (" + placeholders + ") ORDER BY");
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
            List<T> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new InfraestruturaException("Erro ao buscar registros por IDs", e);
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    protected String getSqlListarTodos() {
        return "";
    }
}
