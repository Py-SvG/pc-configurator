package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер базы данных
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:pc_configurator.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("✅ База данных подключена успешно");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Соединение с базой данных закрыто");
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка закрытия соединения: " + e.getMessage());
        }
    }

    public boolean executeUpdate(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка выполнения запроса: " + e.getMessage());
            return false;
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("❌ Ошибка выполнения запроса: " + e.getMessage());
            return null;
        }
    }
}
