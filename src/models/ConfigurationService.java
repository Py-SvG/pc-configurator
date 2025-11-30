package services;

import database.DatabaseManager;
import models.Component;
import models.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с конфигурациями ПК
 */
public class ConfigurationService {
    private DatabaseManager dbManager;
    private ComponentService componentService;

    public ConfigurationService(DatabaseManager dbManager, ComponentService componentService) {
        this.dbManager = dbManager;
        this.componentService = componentService;
    }

    public List<Configuration> getAllConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        String sql = "SELECT * FROM configurations ORDER BY created_date DESC";

        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs != null && rs.next()) {
                Configuration config = resultSetToConfiguration(rs);
                config.setComponents(getConfigurationComponents(config.getId()));
                configurations.add(config);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка получения конфигураций: " + e.getMessage());
        }

        return configurations;
    }

    public Configuration createConfiguration(String name, List<Integer> componentIds) {
        Connection conn = dbManager.getConnection();
        
        try {
            conn.setAutoCommit(false);
            
            // Создаем конфигурацию
            String configSql = "INSERT INTO configurations (name, total_price) VALUES (?, ?)";
            double totalPrice = calculateTotalPrice(componentIds);
            
            try (PreparedStatement pstmt = conn.prepareStatement(configSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, totalPrice);
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                int configId = rs.getInt(1);
                
                // Добавляем компоненты
                addComponentsToConfiguration(configId, componentIds, conn);
                
                conn.commit();
                
                Configuration config = new Configuration(name);
                config.setId(configId);
                config.setTotalPrice(totalPrice);
                config.setComponents(getComponentsByIds(componentIds));
                
                return config;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("❌ Ошибка отката транзакции: " + ex.getMessage());
            }
            System.err.println("❌ Ошибка создания конфигурации: " + e.getMessage());
            return null;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("❌ Ошибка восстановления autocommit: " + e.getMessage());
            }
        }
    }

    private double calculateTotalPrice(List<Integer> componentIds) {
        double total = 0;
        for (int id : componentIds) {
            Component comp = componentService.getComponentById(id);
            if (comp != null) {
                total += comp.getPrice();
            }
        }
        return total;
    }

    private void addComponentsToConfiguration(int configId, List<Integer> componentIds, Connection conn) throws SQLException {
        String sql = "INSERT INTO configuration_items (config_id, component_id, quantity) VALUES (?, ?, 1)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int componentId : componentIds) {
                pstmt.setInt(1, configId);
                pstmt.setInt(2, componentId);
                pstmt.executeUpdate();
            }
        }
    }

    private List<Component> getConfigurationComponents(int configId) {
        List<Component> components = new ArrayList<>();
        String sql = """
            SELECT c.* FROM components c
            JOIN configuration_items ci ON c.id = ci.component_id
            WHERE ci.config_id = ?
        """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, configId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                components.add(componentService.resultSetToComponent(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка получения компонентов конфигурации: " + e.getMessage());
        }

        return components;
    }

    private List<Component> getComponentsByIds(List<Integer> componentIds) {
        List<Component> components = new ArrayList<>();
        for (int id : componentIds) {
            Component comp = componentService.getComponentById(id);
            if (comp != null) {
                components.add(comp);
            }
        }
        return components;
    }

    private Configuration resultSetToConfiguration(ResultSet rs) throws SQLException {
        Configuration config = new Configuration();
        config.setId(rs.getInt("id"));
        config.setName(rs.getString("name"));
        config.setTotalPrice(rs.getDouble("total_price"));
        config.setCreatedDate(rs.getTimestamp("created_date"));
        config.setCompatibilityStatus(rs.getString("compatibility_status"));
        return config;
    }
}
