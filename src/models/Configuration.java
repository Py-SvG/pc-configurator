package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Модель конфигурации ПК
 */
public class Configuration {
    private int id;
    private String name;
    private double totalPrice;
    private Date createdDate;
    private String compatibilityStatus;
    private List<Component> components;

    public Configuration() {
        this.components = new ArrayList<>();
        this.createdDate = new Date();
        this.compatibilityStatus = "UNCHECKED";
    }

    public Configuration(String name) {
        this();
        this.name = name;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getCompatibilityStatus() { return compatibilityStatus; }
    public void setCompatibilityStatus(String compatibilityStatus) { this.compatibilityStatus = compatibilityStatus; }

    public List<Component> getComponents() { return components; }
    public void setComponents(List<Component> components) { this.components = components; }

    public void addComponent(Component component) {
        this.components.add(component);
        this.totalPrice += component.getPrice();
    }

    public void removeComponent(Component component) {
        this.components.remove(component);
        this.totalPrice -= component.getPrice();
    }

    @Override
    public String toString() {
        return String.format("Configuration{id=%d, name='%s', totalPrice=%.2f, components=%d}",
                id, name, totalPrice, components.size());
    }
}
