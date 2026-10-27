package org.example.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class Connect {
    private static final Dotenv ENV = Dotenv.load();

    private static final String URL = ENV.get("DB_URL");
    private static final String USER = ENV.get("DB_USER");
    private static final String PASSWORD = ENV.get("DB_PASSWORD");

    private static Connection connect;

    public static Connection getConnection() {
        try {
            if (connect == null || connect.isClosed()) {
                connect = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connect;
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão com o banco de dados", e);
        }
    }
}