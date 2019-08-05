package zoss.order;

import java.time.LocalDate;
import zoss.database.Database;
import zoss.io.SmartReader;
import zoss.product.Product;
import zoss.util.LinkedList;
import zoss.util.Iterator;
import zoss.util.LinkedListStack;
import zoss.util.LinkedListQueue;
import zoss.util.Queue;
import zoss.util.SkipCommandException;
import zoss.util.Stack;
import zoss.util.StackOverflowException;
import zoss.util.StackUnderflowException;
import zoss.util.QueueOverflowException;
import zoss.util.QueueUnderflowException;
import zoss.util.QuitCommandException;
import static zoss.io.SmartWriter.*;

public class OrderManager {
    private static Queue<Order> orders = new LinkedListQueue<Order>();
    private static Stack<Order> servedOrders = new LinkedListStack<Order>();
    private static int totalOrdersToday = 0;
    
    public static void addOrder() throws QuitCommandException {
        Order newOrder = createOrder();
        try {
            if (newOrder == null) return; // Order cancelled by user or exception occured
            if (newOrder.items() == 0) {
                displayError("<< Empty Cart >>\n");
                return;
            }
            orders.enqueue(newOrder);
            totalOrdersToday++;
            if (orders.size() == 1) display("Order #%d is placed for amount %.2f.", newOrder.getId(), newOrder.getAmount());
            else display("Order #%d is placed for amount %.2f. Will be served after %d customer%s.", newOrder.getId(), newOrder.getAmount(), orders.size() - 1, orders.size() > 2 ? "s" : "");
        } catch (QueueOverflowException qoe) {
            displayError("Queue if full! A busy day huh?!");
        }
        displayBlank();
    }
    
    public static Order createOrder() throws QuitCommandException {
        LocalDate today = LocalDate.now();
        SmartReader reader = new SmartReader(System.in);
        Order order = new Order(today.getYear() % (today.getYear() / 100 * 100) * 10000000 + today.getMonthValue() * 100000 + today.getDayOfMonth() * 1000 + totalOrdersToday);
        LinkedList<Product> menu = loadMenu();
        LinkedList<Integer> cart = new LinkedList<Integer> (0, menu.size());
        double sum = 0;
        while (true) {
            sum = 0;
            Iterator<Product> menuViewer = menu.iterator();
            Iterator<Integer> cartViewer = cart.iterator();
            display(" %-10s  %-35s  %-8s  %-8s  %-10s", "SN", "Item", "In Cart", "In Stock", "Price");
            for (int sn = 1; menuViewer.hasNext(); menuViewer.progress(), cartViewer.progress(), sn++) {
                Product selectedProduct = menuViewer.getData();
                if (cartViewer.getData() == 0) {
                    display("#%-10s  %-35s  %-8s  %-8s  %-10.2f", sn, selectedProduct.getName(), cartViewer.getData(), selectedProduct.getQuantity(), selectedProduct.getPrice());
                } else {
                    displayError("#%-10s  %-35s  %-8s  %-8s  %-10.2f", sn, selectedProduct.getName(), cartViewer.getData(), selectedProduct.getQuantity(), selectedProduct.getPrice());
                    sum += cartViewer.getData() * selectedProduct.getPrice();
                }
            }
            display("#%-10s  %-35s  %18s  %-10.2f", 0, "Place an Order", "Total:", sum);
            displayError("\n#%-10s  %-35s\n", -1, "Cancel Order");
            display("Please select an item by SN");
            int selectedIndex;
            try {
                while ((selectedIndex = reader.readInt()) < -1 || selectedIndex > menu.size()) displayError("Please choice a valid product (by SN) from the above list.");
                if (selectedIndex == -1) {
                    Iterator<Integer> stock = cart.iterator();
                    for (Iterator<Product> prod = menu.iterator(); prod.hasNext(); prod.progress(), stock.progress()) {
                        prod.getData().sell(-stock.getData());
                    }
                    displayError("Order cancelled by the user.\n");
                    return null;
                }
                if (selectedIndex == 0) break;
                Product selectedProduct = menu.get(--selectedIndex);
                Integer selectedCart = cart.get(selectedIndex);
                display("Please enter the quantity for %s", selectedProduct.getName());
                int quantity = reader.readInt();
                if (quantity > selectedProduct.getQuantity()) displayError("Out of stock. We will refill soon.\n");
                else if (selectedCart + quantity < 0) {
                    cart.set(selectedIndex, 0);
                    selectedProduct.sell(-selectedCart);
                } else {
                    cart.set(selectedIndex, selectedCart + quantity);
                    selectedProduct.sell(quantity);
                }
            } catch (SkipCommandException e) {
                displayError(e.getMessage());
                return null;
            }
        }
        LinkedList<Product> selectedItems = new LinkedList<Product>();
        LinkedList<Integer> quantity = new LinkedList<Integer>();
        Iterator<Product> menuViewer = menu.iterator();
        Iterator<Integer> cartViewer = cart.iterator();
        for (; cartViewer.hasNext(); menuViewer.progress(), cartViewer.progress()) {
            if (cartViewer.getData() > 0) {
                selectedItems.insert(menuViewer.getData(), selectedItems.size());
                quantity.insert(cartViewer.getData(), quantity.size());
            }
        }
        order.addProducts(selectedItems, quantity, sum);
        return order;
    }
    
    public static LinkedList<Product> loadMenu() {
        LinkedList<Product> menu;
        menu = Database.loadProducts();
        return menu;
    }
    
    public static void showOrders() {
        if (orders.isEmpty()) {
            displayError("<< No orders pending >>\n");
        } else {
            display("Pending orders:");
            for (Iterator <Order> i = orders.iterator(); i.hasNext(); i.progress()) {
                display("# %s", i.getData());
            }
            displayBlank();
        }
    }
    
    public static void serve() {
        try {
            display("Serving... Order #%d Tk.%.2f", orders.peek().getId(), orders.peek().getAmount());
            servedOrders.push(orders.dequeue());
            display("Order #%d has been served successfully.", servedOrders.peek().getId());
        } catch (QueueUnderflowException que) {
            displayError("<< No orders pending >>");
        } catch (StackOverflowException soe) {
            displayError("<< Maximum serving limit reached >>");
        } catch (StackUnderflowException sue) {
            displayError("<< No orders has been served yet >>");
        }
        displayBlank();
    }
    
    public static void undoLastServed() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        try {
            display("Are you sure you want to undo last served order (#%d)? It will be served after serving currently pending orders.\n\t1. Yes\n\t2. No", servedOrders.peek().getId());
            int choice;
            while ((choice = reader.readInt()) < 1 || choice > 2) displayError("Please choose a valid option from above");
            if (choice == 1) {
                orders.enqueue(servedOrders.pop());
            }
        } catch (SkipCommandException sce) {
            display(sce.getMessage() + "\n");
        } catch (StackUnderflowException sue) {
            displayError("<< No orders has been served yet >>");
        } catch (QueueOverflowException qoe) {
            displayError("<< Queue is full >>");
        }
    }
    
    public static void peekNextOrder() {
        try {
            Order nextOrder = orders.peek();
            display("Order: #%d", nextOrder.getId());
            display(" %-10s  %-35s  %-8s  %-10s  %-10s", "SN", "Item", "Quantity", "Unit Price", "Price");
            Iterator <Product> product = nextOrder.getProducts().iterator();
            Iterator <Integer> quantity = nextOrder.getCart().iterator();
            for (int sn = 1; product.hasNext(); product.progress(), quantity.progress()) {
                if (quantity.getData() > 0) display("#%-10s  %-35s  %-8s  %-10s  %-10s", sn++, product.getData().getName(), quantity.getData(), product.getData().getPrice(), quantity.getData() * product.getData().getPrice());
            }
            display("Total: Tk. %.2f", nextOrder.getAmount());
        } catch (QueueUnderflowException que) {
            displayError("<< No pending orders >>");
        }
        displayBlank();
    }
    
    public static void peekLastOrder() {
        try {
            Order lastOrder = servedOrders.peek();
            display("Order: #%d", lastOrder.getId());
            display(" %-10s  %-35s  %-8s  %-10s  %-10s", "SN", "Item", "Quantity", "Unit Price", "Price");
            Iterator <Product> product = lastOrder.getProducts().iterator();
            Iterator <Integer> quantity = lastOrder.getCart().iterator();
            for (int sn = 1; product.hasNext(); product.progress(), quantity.progress()) {
                if (quantity.getData() > 0) display("#%-10s  %-35s  %-8s  %-10s  %-10s", sn++, product.getData().getName(), quantity.getData(), product.getData().getPrice(), quantity.getData() * product.getData().getPrice());
            }
            display("Total: Tk. %.2f", lastOrder.getAmount());
        } catch (StackUnderflowException que) {
            displayError("<< No orders has been served yet >>");
        }
        displayBlank();
    }
    
    public static int numberOfPendingOrders() {
        return orders.size();
    }
    
    public static int numberOfServedOrders() {
        return servedOrders.size();
    }
}