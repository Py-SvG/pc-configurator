package services;

import database.DatabaseManager;
import models.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с комплектующими
 */
public class ComponentService {
    private DatabaseManager dbManager;

    public ComponentService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Component> getAllComponents() {
        List<Component> components = new ArrayList<>();
        String sql = "SELECT * FROM components ORDER BY type, name";

        try (ResultSet rs = dbManager.executeQuery(sql)) {
            while (rs != null && rs.next()) {
                components.add(resultSetToComponent(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка получения комплектующих: " + e.getMessage());
        }

        return components;
    }

    public Component getComponentById(int id) {
        String sql = "SELECT * FROM components WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetToComponent(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка получения комплектующего: " + e.getMessage());
        }
        
        return null;
    }

    public boolean addComponent(Component component) {
        String sql = """
            INSERT INTO components (name, type, price, quantity, specifications, 
                                  socket, memory_type, form_factor, power_requirements, max_memory)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            setComponentParameters(pstmt, component);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка добавления комплектующего: " + e.getMessage());
            return false;
        }
    }

    public boolean updateComponent(Component component) {
        String sql = """
            UPDATE components SET name=?, type=?, price=?, quantity=?, specifications=?,
                                socket=?, memory_type=?, form_factor=?, power_requirements=?, max_memory=?
            WHERE id=?
        """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            setComponentParameters(pstmt, component);
            pstmt.setInt(11, component.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка обновления комплектующего: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteComponent(int id) {
        String sql = "DELETE FROM components WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка удаления комплектующего: " + e.getMessage());
            return false;
        }
    }

    private Component resultSetToComponent(ResultSet rs) throws SQLException {
        Component component = new Component();
        component.setId(rs.getInt("id"));
        component.setName(rs.getString("name"));
        component.setType(rs.getString("type"));
        component.setPrice(rs.getDouble("price"));
        component.setQuantity(rs.getInt("quantity"));
        component.setSpecifications(rs.getString("specifications"));
        component.setSocket(rs.getString("socket"));
        component.setMemoryType(rs.getString("memory_type"));
        component.setFormFactor(rs.getString("form_factor"));
        component.setPowerRequirements(rs.getInt("power_requirements"));
        component.setMaxMemory(rs.getInt("max_memory"));
        return component;
    }

    private void setComponentParameters(PreparedStatement pstmt, Component component) throws SQLException {
        pstmt.setString(1, component.getName());
        pstmt.setString(2, component.getType());
        pstmt.setDouble(3, component.getPrice());
        pstmt.setInt(4, component.getQuantity());
        pstmt.setString(5, component.getSpecifications());
        pstmt.setString(6, component.getSocket());
        pstmt.setString(7, component.getMemoryType());
        pstmt.setString(8, component.getFormFactor());
        pstmt.setInt(9, component.getPowerRequirements());
        pstmt.setInt(10, component.getMaxMemory());
    }
}
