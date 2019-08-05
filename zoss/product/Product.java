package zoss.product;

import static zoss.io.SmartWriter.*;
import static zoss.util.SmartSearch.*;

public class Product {
    private String id, name;
    private int quantity;
    private double price;
    
    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public Product(String[] line) {
        this(line[0], line[1], Double.parseDouble(line[2]), Integer.parseInt(line[3]));
    }
    
    // Checks if product is not out of stock then pulls one from the stock and return the price of the product
    public double sell() {
        if (quantity == 0) {
            displayError("%s is out of stock.", name);
            return 0;
        }
        quantity--;
        // db.updateProduct(this);
        return price;
    }
    
    // Checks if product is not out of stock then pulls n products from the stock and return the sum of price of the product
    public double sell(int n) {
        if (quantity < n) {
            displayError("%s is out of stock.", name);
            return 0;
        }
        quantity -= n;
        ProductManager.updateProduct(this);
        return price * n;
    }
    
    // Takes number of item added and return the total after addition
    public int add(int quantity) {
        // db.updateProduct(this);
        return this.quantity += quantity;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getId() {
        return id; 
    }
    
    public String getName() {
        return name;
    }
    
    public String getPreferredLengthName(int length) {
        if (length < 5) return ".....".substring(0, length);
        int xFactor = length * 5 / 10 + 3;
        if (name.length() <= length) return name;
        else return name.substring(0, length * 5 / 10) + "..." + name.substring(name.length() - (length - xFactor), name.length());
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public boolean nameSimilarTo(String filter) {
        return getSimilarityPercentage(name.toLowerCase(), filter.toLowerCase()) >= 50;
    }
    
    public boolean idSimilarTo(String filter) {
        return getSimilarityPercentage(id.toLowerCase(), filter.toLowerCase()) >= 50;
    }
    
    // Return a String representing the detail of the product
    public String toString() {
        return id + SEPARATOR + name + SEPARATOR + price + SEPARATOR + quantity;
    }
}