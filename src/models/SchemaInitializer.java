package database;

import java.sql.*;

/**
 * Инициализатор схемы базы данных
 */
public class SchemaInitializer {
    private DatabaseManager dbManager;

    public SchemaInitializer(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        createTables();
        insertSampleData();
        insertCompatibilityRules();
    }

    private void createTables() {
        String[] createTables = {
            """
            CREATE TABLE IF NOT EXISTS components (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                quantity INTEGER NOT NULL,
                specifications TEXT,
                socket TEXT,
                memory_type TEXT,
                form_factor TEXT,
                power_requirements INTEGER,
                max_memory INTEGER
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS configurations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                total_price DECIMAL(10,2) NOT NULL,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                compatibility_status TEXT DEFAULT 'UNCHECKED'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS configuration_items (
                config_id INTEGER,
                component_id INTEGER,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (config_id) REFERENCES configurations(id),
                FOREIGN KEY (component_id) REFERENCES components(id),
                PRIMARY KEY (config_id, component_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS compatibility_rules (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                rule_type TEXT NOT NULL,
                component_type1 TEXT NOT NULL,
                component_type2 TEXT NOT NULL,
                condition TEXT NOT NULL,
                description TEXT NOT NULL
            )
            """
        };

        for (String sql : createTables) {
            dbManager.executeUpdate(sql);
        }
        System.out.println("✅ Таблицы базы данных созданы успешно");
    }

    private void insertSampleData() {
        try {
            // Проверяем, есть ли уже данные
            ResultSet rs = dbManager.executeQuery("SELECT COUNT(*) FROM components");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                String[] components = {
                    "INSERT INTO components (name, type, price, quantity, specifications, socket, memory_type, power_requirements) VALUES " +
                    "('Intel Core i7-13700K', 'Процессор', 35000, 10, '16 ядер, 3.4 ГГц', 'LGA1700', 'DDR5', 125)",
                    
                    "INSERT INTO components (name, type, price, quantity, specifications, socket, memory_type, form_factor) VALUES " +
                    "('ASUS ROG Strix B650', 'Материнская плата', 25000, 15, 'Socket AM5, DDR5', 'AM5', 'DDR5', 'ATX')",
                    
                    "INSERT INTO components (name, type, price, quantity, specifications, memory_type) VALUES " +
                    "('Kingston Fury 32GB DDR5', 'Оперативная память', 12000, 20, '32GB, 5600MHz', 'DDR5')"
                };

                for (String sql : components) {
                    dbManager.executeUpdate(sql);
                }
                System.out.println("✅ Тестовые данные добавлены успешно");
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка добавления тестовых данных: " + e.getMessage());
        }
    }

    private void insertCompatibilityRules() {
        String[] rules = {
            "INSERT OR IGNORE INTO compatibility_rules (rule_type, component_type1, component_type2, condition, description) VALUES " +
            "('SOCKET_MATCH', 'Процессор', 'Материнская плата', 'socket = socket', 'Сокет процессора должен соответствовать сокету материнской платы')"
        };

        for (String sql : rules) {
            dbManager.executeUpdate(sql);
        }
        System.out.println("✅ Правила совместимости добавлены");
    }
}
