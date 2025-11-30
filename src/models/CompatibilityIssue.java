package models;

/**
 * Модель проблемы совместимости
 */
public class CompatibilityIssue {
    private String type;
    private String description;
    private String severity; // ERROR, WARNING, INFO

    public CompatibilityIssue(String type, String description, String severity) {
        this.type = type;
        this.description = description;
        this.severity = severity;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String toString() {
        String icon = "ℹ️";
        if ("ERROR".equals(severity)) icon = "❌";
        else if ("WARNING".equals(severity)) icon = "⚠️";
        
        return String.format("%s [%s] %s: %s", icon, severity, type, description);
    }
}
