package zoss.order;

import zoss.product.Product;
import zoss.util.LinkedList;

public class Order {
    private LinkedList<Product> products = new LinkedList<Product>();
    private LinkedList<Integer> cart = new LinkedList<Integer>();
    private int id;
    private double amount;
    private String status;
    
    public Order(int id) {
        this.id = id;
        status = "Pending";
    }
    
    public void addProducts(LinkedList<Product> products, LinkedList<Integer> cart, double amount) {
        this.products = products;
        this.cart = cart;
        this.amount = amount;
    }
    
    public void addProducts(LinkedList<Product> products, LinkedList<Integer> cart) {
        this.products = products;
        this.cart = cart;
        for (int i = 0; i < products.size(); i++) {
            amount += products.get(i).getPrice() * cart.get(i);
        }
    }
    
    public void addProduct(Product product, Integer quantity) {
        products.insert(product, products.size());
        amount += product.getPrice() * quantity;
    }
    
    // Under construction;
    public void removeProduct(Product product) {
        
    }
    
    public int getId() {
        return id;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public int items() {
        return products.size();
    }
    
    public void serve() {
        status = "Served";
    }
    
    public LinkedList<Product> getProducts() {
        LinkedList<Product> copyOfProducts = new LinkedList<Product>(products.copyList());
        
        return copyOfProducts;
    }
    
    public LinkedList<Integer> getCart() {
        LinkedList<Integer> copyOfCart = new LinkedList<Integer>(cart.copyList());
        
        return copyOfCart;
    }
    
    public String toString() {
        return id + " - Tk. " + String.format("%.2f - ", amount) + status;
    }
}