package models;

/**
 * Модель комплектующего ПК
 */
public class Component {
    private int id;
    private String name;
    private String type;
    private double price;
    private int quantity;
    private String specifications;
    private String socket;
    private String memoryType;
    private String formFactor;
    private int powerRequirements;
    private int maxMemory;

    public Component() {}

    public Component(String name, String type, double price, int quantity, 
                    String specifications, String socket, String memoryType, 
                    String formFactor, int powerRequirements, int maxMemory) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.specifications = specifications;
        this.socket = socket;
        this.memoryType = memoryType;
        this.formFactor = formFactor;
        this.powerRequirements = powerRequirements;
        this.maxMemory = maxMemory;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getSocket() { return socket; }
    public void setSocket(String socket) { this.socket = socket; }

    public String getMemoryType() { return memoryType; }
    public void setMemoryType(String memoryType) { this.memoryType = memoryType; }

    public String getFormFactor() { return formFactor; }
    public void setFormFactor(String formFactor) { this.formFactor = formFactor; }

    public int getPowerRequirements() { return powerRequirements; }
    public void setPowerRequirements(int powerRequirements) { this.powerRequirements = powerRequirements; }

    public int getMaxMemory() { return maxMemory; }
    public void setMaxMemory(int maxMemory) { this.maxMemory = maxMemory; }

    @Override
    public String toString() {
        return String.format("Component{id=%d, name='%s', type='%s', price=%.2f, quantity=%d}",
                id, name, type, price, quantity);
    }
}
