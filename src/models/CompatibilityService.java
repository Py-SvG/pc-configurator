package services;

import database.DatabaseManager;
import models.Component;
import models.CompatibilityIssue;
import models.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для проверки совместимости компонентов
 */
public class CompatibilityService {
    private DatabaseManager dbManager;

    public CompatibilityService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<CompatibilityIssue> validateConfiguration(Configuration configuration) {
        List<CompatibilityIssue> issues = new ArrayList<>();
        List<Component> components = configuration.getComponents();

        // Проверяем основные правила совместимости
        issues.addAll(checkSocketCompatibility(components));
        issues.addAll(checkMemoryCompatibility(components));
        issues.addAll(checkFormFactorCompatibility(components));
        issues.addAll(checkPowerSupplyCompatibility(components));

        return issues;
    }

    private List<CompatibilityIssue> checkSocketCompatibility(List<Component> components) {
        List<CompatibilityIssue> issues = new ArrayList<>();
        
        Component cpu = findComponentByType(components, "Процессор");
        Component motherboard = findComponentByType(components, "Материнская плата");
        
        if (cpu != null && motherboard != null) {
            if (cpu.getSocket() != null && motherboard.getSocket() != null && 
                !cpu.getSocket().equals(motherboard.getSocket())) {
                issues.add(new CompatibilityIssue(
                    "SOCKET_MISMATCH",
                    String.format("Несовместимость сокетов: Процессор %s (сокет %s) не подходит к материнской плате %s (сокет %s)",
                                 cpu.getName(), cpu.getSocket(), motherboard.getName(), motherboard.getSocket()),
                    "ERROR"
                ));
            }
        }
        
        return issues;
    }

    private List<CompatibilityIssue> checkMemoryCompatibility(List<Component> components) {
        List<CompatibilityIssue> issues = new ArrayList<>();
        
        Component memory = findComponentByType(components, "Оперативная память");
        Component motherboard = findComponentByType(components, "Материнская плата");
        
        if (memory != null && motherboard != null) {
            if (memory.getMemoryType() != null && motherboard.getMemoryType() != null && 
                !memory.getMemoryType().equals(motherboard.getMemoryType())) {
                issues.add(new CompatibilityIssue(
                    "MEMORY_MISMATCH",
                    String.format("Несовместимость памяти: Оперативная память %s (%s) не поддерживается материнской платой %s (%s)",
                                 memory.getName(), memory.getMemoryType(), motherboard.getName(), motherboard.getMemoryType()),
                    "ERROR"
                ));
            }
        }
        
        return issues;
    }

    private List<CompatibilityIssue> checkFormFactorCompatibility(List<Component> components) {
        List<CompatibilityIssue> issues = new ArrayList<>();
        
        Component motherboard = findComponentByType(components, "Материнская плата");
        Component chassis = findComponentByType(components, "Корпус");
        
        if (motherboard != null && chassis != null) {
            if (!isFormFactorCompatible(motherboard.getFormFactor(), chassis.getFormFactor())) {
                issues.add(new CompatibilityIssue(
                    "FORM_FACTOR_MISMATCH",
                    String.format("Несовместимость форм-факторов: Материнская плата %s (%s) не помещается в корпус %s (%s)",
                                 motherboard.getName(), motherboard.getFormFactor(), chassis.getName(), chassis.getFormFactor()),
                    "ERROR"
                ));
            }
        }
        
        return issues;
    }

    private List<CompatibilityIssue> checkPowerSupplyCompatibility(List<Component> components) {
        List<CompatibilityIssue> issues = new ArrayList<>();
        
        Component psu = findComponentByType(components, "Блок питания");
        if (psu == null) {
            issues.add(new CompatibilityIssue("NO_PSU", "Отсутствует блок питания", "ERROR"));
            return issues;
        }
        
        int totalPower = calculateTotalPowerConsumption(components);
        
        if (psu.getPowerRequirements() < totalPower) {
            issues.add(new CompatibilityIssue(
                "INSUFFICIENT_POWER",
                String.format("Недостаточная мощность БП: требуется %dW, а блок питания %s имеет %dW",
                             totalPower, psu.getName(), psu.getPowerRequirements()),
                "ERROR"
            ));
        } else if (psu.getPowerRequirements() < totalPower + 100) {
            issues.add(new CompatibilityIssue(
                "LOW_POWER_MARGIN",
                String.format("Малая мощность БП: рекомендуется запас 100W+ (текущий запас: %dW)",
                             psu.getPowerRequirements() - totalPower),
                "WARNING"
            ));
        }
        
        return issues;
    }

    private int calculateTotalPowerConsumption(List<Component> components) {
        int totalPower = 0;
        for (Component comp : components) {
            if (comp.getPowerRequirements() > 0 && 
                (comp.getType().equals("Процессор") || comp.getType().equals("Видеокарта"))) {
                totalPower += comp.getPowerRequirements();
            }
        }
        // Добавляем запас 100W для остальных компонентов
        return totalPower + 100;
    }

    private Component findComponentByType(List<Component> components, String type) {
        return components.stream()
                .filter(c -> c.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private boolean isFormFactorCompatible(String motherboardFF, String chassisFF) {
        if (motherboardFF == null || chassisFF == null) return true;
        
        return switch (motherboardFF) {
            case "ATX" -> chassisFF.equals("ATX") || chassisFF.equals("E-ATX");
            case "Micro-ATX" -> chassisFF.equals("ATX") || chassisFF.equals("Micro-ATX");
            case "Mini-ITX" -> chassisFF.equals("ATX") || chassisFF.equals("Micro-ATX") || chassisFF.equals("Mini-ITX");
            default -> true;
        };
    }
}
