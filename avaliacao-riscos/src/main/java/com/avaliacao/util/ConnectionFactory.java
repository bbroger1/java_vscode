package com.avaliacao.util;

import com.avaliacao.exception.InfraestruturaException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String JNDI_NAME = "java:comp/env/jdbc/AvaliacaoRiscosDS";

    private ConnectionFactory() {
    }

    private static DataSource dataSource;

    private static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                Context initContext = new InitialContext();
                dataSource = (DataSource) initContext.lookup(JNDI_NAME);
            } catch (Exception e) {
                throw new InfraestruturaException(
                    "Erro ao obter DataSource JNDI: " + JNDI_NAME, e);
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
