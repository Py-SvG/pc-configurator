import database.DatabaseManager;
import database.SchemaInitializer;
import models.Component;
import models.CompatibilityIssue;
import models.Configuration;
import services.*;
import utils.ConsoleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Главный класс приложения - конфигуратор ПК
 */
public class Main {
    private DatabaseManager dbManager;
    private ComponentService componentService;
    private ConfigurationService configurationService;
    private CompatibilityService compatibilityService;

    public static void main(String[] args) {
        System.out.println("🚀 Запуск PC Configurator...");
        Main app = new Main();
        app.initialize();
        app.run();
    }

    private void initialize() {
        // Инициализация базы данных и сервисов
        dbManager = new DatabaseManager();
        new SchemaInitializer(dbManager);
        
        componentService = new ComponentService(dbManager);
        configurationService = new ConfigurationService(dbManager, componentService);
        compatibilityService = new CompatibilityService(dbManager);
        
        ConsoleUtils.printSuccess("Система инициализирована успешно");
    }

    private void run() {
        while (true) {
            showMainMenu();
            int choice = ConsoleUtils.readInt("Выберите действие");
            
            switch (choice) {
                case 1 -> showAllComponents();
                case 2 -> addComponent();
                case 3 -> createConfiguration();
                case 4 -> showConfigurations();
                case 5 -> checkCompatibility();
                case 6 -> searchComponents();
                case 7 -> showInventoryReport();
                case 0 -> {
                    shutdown();
                    return;
                }
                default -> ConsoleUtils.printError("Неверный выбор");
            }
            
            ConsoleUtils.pressEnterToContinue();
        }
    }

    private void showMainMenu() {
        ConsoleUtils.printHeader("КОНФИГУРАТОР ПК");
        System.out.println("1. 📋 Просмотр комплектующих");
        System.out.println("2. ➕ Добавить комплектующее");
        System.out.println("3. 🔧 Создать конфигурацию ПК");
        System.out.println("4. 📁 Просмотр конфигураций");
        System.out.println("5. 🔍 Проверить совместимость");
        System.out.println("6. 🔎 Поиск комплектующих");
        System.out.println("7. 📊 Отчет по остаткам");
        System.out.println("0. 🚪 Выход");
    }

    private void showAllComponents() {
        ConsoleUtils.printHeader("ВСЕ КОМПЛЕКТУЮЩИЕ");
        List<Component> components = componentService.getAllComponents();
        
        if (components.isEmpty()) {
            ConsoleUtils.printInfo("Комплектующие не найдены");
            return;
        }
        
        System.out.printf("%-3s %-25s %-15s %-10s %-8s %-10s %s%n", 
            "ID", "Название", "Тип", "Цена", "Кол-во", "Сокет", "Характеристики");
        System.out.println("-".repeat(100));
        
        for (Component comp : components) {
            System.out.printf("%-3d %-25s %-15s %-10.2f %-8d %-10s %s%n",
                comp.getId(),
                comp.getName(),
                comp.getType(),
                comp.getPrice(),
                comp.getQuantity(),
                comp.getSocket() != null ? comp.getSocket() : "-",
                comp.getSpecifications());
        }
    }

    private void addComponent() {
        ConsoleUtils.printHeader("ДОБАВЛЕНИЕ КОМПЛЕКТУЮЩЕГО");
        
        String name = ConsoleUtils.readString("Название");
        String type = ConsoleUtils.readString("Тип (Процессор/Материнская плата/Оперативная память/...)");
        double price = ConsoleUtils.readDouble("Цена");
        int quantity = ConsoleUtils.readInt("Количество");
        String specs = ConsoleUtils.readString("Характеристики");
        
        Component component = new Component(name, type, price, quantity, specs, null, null, null, 0, 0);
        
        if (componentService.addComponent(component)) {
            ConsoleUtils.printSuccess("Комплектующее добавлено успешно");
        } else {
            ConsoleUtils.printError("Ошибка при добавлении комплектующего");
        }
    }

    private void createConfiguration() {
        ConsoleUtils.printHeader("СОЗДАНИЕ КОНФИГУРАЦИИ");
        
        String name = ConsoleUtils.readString("Название конфигурации");
        showAllComponents();
        
        List<Integer> componentIds = new ArrayList<>();
        while (true) {
            int componentId = ConsoleUtils.readInt("Введите ID комплектующего (0 для завершения)");
            if (componentId == 0) break;
            
            Component comp = componentService.getComponentById(componentId);
            if (comp != null) {
                componentIds.add(componentId);
                ConsoleUtils.printSuccess("Добавлен: " + comp.getName());
            } else {
                ConsoleUtils.printError("Комплектующее не найдено");
            }
        }
        
        if (componentIds.isEmpty()) {
            ConsoleUtils.printError("Не выбрано ни одного комплектующего");
            return;
        }
        
        Configuration config = configurationService.createConfiguration(name, componentIds);
        if (config != null) {
            ConsoleUtils.printSuccess("Конфигурация создана: " + config.getName());
            ConsoleUtils.printInfo("Общая стоимость: " + config.getTotalPrice() + " руб.");
        }
    }

    private void showConfigurations() {
        ConsoleUtils.printHeader("СОХРАНЕННЫЕ КОНФИГУРАЦИИ");
        List<Configuration> configs = configurationService.getAllConfigurations();
        
        if (configs.isEmpty()) {
            ConsoleUtils.printInfo("Конфигурации не найдены");
            return;
        }
        
        for (Configuration config : configs) {
            System.out.printf("ID: %d | Название: %s | Стоимость: %.2f руб. | Статус: %s%n",
                config.getId(), config.getName(), config.getTotalPrice(), config.getCompatibilityStatus());
            
            System.out.println("Компон
