package br.com.glicemia.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static String dbDriver;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        if (EnvLoader.isLoaded()) {
            dbUrl = EnvLoader.get("DB_URL");
            dbUsername = EnvLoader.get("DB_USERNAME");
            dbPassword = EnvLoader.get("DB_PASSWORD");
            dbDriver = EnvLoader.get("DB_DRIVER");
        } else {
            loadFromProperties();
        }

        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL não encontrado", e);
        }
    }

    private static void loadFromProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo database.properties não encontrado");
            }
            properties.load(input);

            dbUrl = resolveProperty(properties.getProperty("db.url"));
            dbUsername = resolveProperty(properties.getProperty("db.username"));
            dbPassword = resolveProperty(properties.getProperty("db.password"));
            dbDriver = resolveProperty(properties.getProperty("db.driver"));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações do banco de dados", e);
        }
    }

    private static String resolveProperty(String value) {
        if (value == null) {
            return null;
        }

        if (value.startsWith("${") && value.endsWith("}")) {
            String envKey = value.substring(2, value.length() - 1);
            String envValue = EnvLoader.get(envKey);

            if (envValue == null) {
                envValue = System.getenv(envKey);
            }

            return envValue != null ? envValue : value;
        }

        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String getDatabaseVersion() {
        try (Connection conn = getConnection()) {
            var meta = conn.getMetaData();
            return meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion();
        } catch (SQLException e) {
            return "Erro ao obter versão: " + e.getMessage();
        }
    }
}
