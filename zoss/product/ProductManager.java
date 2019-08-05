package zoss.product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import zoss.database.Database;
import zoss.io.SmartReader;
import zoss.util.Iterator;
import zoss.util.LinkedList;
import zoss.util.SkipCommandException;
import zoss.util.QuitCommandException;
import static zoss.io.SmartWriter.*;

public class ProductManager {    
    public static boolean editProduct() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        Product currentProduct;
        display("Product ID/ name");
        int changes = 0;
        try {
            String filter = reader.readLine();
            LinkedList<Product> filteredProducts = Database.loadProducts(filter);
            if (filteredProducts.size() == 0) {
                displayError("<< No results found >>\n");
                return false;
            } else {
                display("Please choose a product from the list below:\n");
                for (int i = 0; i < filteredProducts.size(); i++) {
                    display("#%d %s (ID: %s)", i + 1, filteredProducts.get(i).getName(), filteredProducts.get(i).getId());
                }
                int choice;
                while ((choice = reader.readInt()) < 1 || choice > filteredProducts.size()) displayError("Please choose a valid index from 1 to %d", filteredProducts.size());
                currentProduct = filteredProducts.get(choice - 1);
//                display("Selected Product: %s\n", currentProduct.getName()); // Outdated
                while (true) {
                    display("Selected Product: %s (Left %d) | Tk. %.2f\n", currentProduct.getName(), currentProduct.getQuantity(), currentProduct.getPrice());
                    display("Please choose an option from the below:\n\n" + 
                            "\t1. Change Name\n" +
                            "\t2. Change Price\n" +
                            "\t3. Add Stock\n" +
                            "\t4. Set Stock\n" +
                            "\t5. Cancel\n" +
                            "\t0. Save Changes\n");
                    int selectedOption;
                    try { 
                        while ((selectedOption = reader.readInt()) < 0 || selectedOption > 5) displayError("Please choose a valid option from the above list"); 
                        if (selectedOption == 0) break;
                        else if (selectedOption == 1) changes += changeName(currentProduct) ? 1 : 0;
                        else if (selectedOption == 2) changes += changePrice(currentProduct) ? 1 : 0;
                        else if (selectedOption == 3) changes += addStock(currentProduct) ? 1 : 0;
                        else if (selectedOption == 4) changes += setStock(currentProduct) ? 1 : 0;
                        else if (selectedOption == 5) return false; // Cancel
                    } catch (SkipCommandException sce) {
                        displayError("Skip Commands are not allowed here.\n");
                    }
                }
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        
        if (updateProduct(currentProduct)) display("%s" , changes == 0 ? "<< No Changes Made So Far >>\n" : "(" + changes + ") changes are saved successfully.\n");
        else return false;
        
        return true;
    }
    
    public static void showAllProducts() {
        LinkedList<Product> products = Database.loadProducts();
        if (products.size() == 0) {
            display("<< No products found >>");
        } else {
            display(" %-12s  %-27s  %-10s  %-10s", "Product ID", "Name", "In Stock", "Price (Taka)");
            for (int i = 0; i < products.size(); i++) {
                display("#%-12s  %-27s  %-10d  %-10.2f", products.get(i).getId(), products.get(i).getPreferredLengthName(25), products.get(i).getQuantity(), products.get(i).getPrice());
            }
        }
        displayBlank();
    }
    
    public static void showFilteredProducts() throws QuitCommandException {
        display("Product ID/ name");
        SmartReader reader = new SmartReader(System.in);
        try {
            String filter = reader.readLine();
            LinkedList<Product> products = Database.loadProducts(filter);
            if (products.size() == 0) {
                display("<< No products found >>");
            } else {
                display(" %-12s  %-27s  %-10s  %-10s", "Product ID", "Name", "Quantity", "Price");
                for (int i = 0; i < products.size(); i++) {
                    display("#%-12s  %-27s  %-10d  %-10.2f", products.get(i).getId(), products.get(i).getPreferredLengthName(25), products.get(i).getQuantity(), products.get(i).getPrice());
                }
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        displayBlank();
    }
    
    public static boolean updateProduct(Product product) {
        try {
            BufferedReader productDB = new BufferedReader(new FileReader("zoss/database/Products.pcdb"));
            StringBuffer update = new StringBuffer();
            String line;
            while ((line = productDB.readLine()) != null) {
                if (line.startsWith(product.getId())) update.append(product + System.lineSeparator());
                else update.append(line + System.lineSeparator());
            }
            productDB.close();
            FileOutputStream updatedDB = new FileOutputStream("zoss/database/Products.pcdb");
            updatedDB.write(update.toString().getBytes());
            updatedDB.close();
            return true;
        } catch (FileNotFoundException e) {
            displayError("Database is missing.");
            return false;
        } catch (IOException e) {
            displayError("Unknown error occured during updating the product (ID: %s) database. Please contact with the administrator.", product.getId());
            return false;
        }
    }
    
    public static boolean deleteProduct() throws QuitCommandException {
        display("Product ID/ name");
        SmartReader reader = new SmartReader(System.in);
        try {
            String filter = reader.readLine();
            LinkedList<Product> products = Database.loadProducts(filter);
            if (products.size() == 0) {
                displayError("<< No results found >>\n");
                return false;
            } else {
                display("Please choose a product from the list below:\n");
                for (int i = 0; i < products.size(); i++) {
                    display("#%d %s (ID: %s)", i + 1, products.get(i).getName(), products.get(i).getId());
                }
                int choice;
                while ((choice = reader.readInt()) < 1 || choice > products.size()) displayError("Please choose a valid index from 1 to %d", products.size());
                Product currentProduct = products.get(choice - 1);
                display("Are you sure you want to delete the product?\n\t1. Yes\n\t2. No");
                while ((choice = reader.readInt()) < 1 || choice > 2) displayError("Please choose a valid option from the above");
                if (choice == 1) return deleteProduct(currentProduct);
                return false;
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
    }
    
    public static boolean deleteProduct(Product product) {
        try {
            BufferedReader productDB = new BufferedReader(new FileReader("zoss/database/Products.pcdb"));
            StringBuffer update = new StringBuffer();
            String line;
            while ((line = productDB.readLine()) != null) {
                if (!line.startsWith(product.getId())) update.append(line + System.lineSeparator());
            }
            productDB.close();
            FileOutputStream updatedDB = new FileOutputStream("zoss/database/Products.pcdb");
            updatedDB.write(update.toString().getBytes());
            updatedDB.close();
            display("Product deleted successfully.\n");
            return true;
        } catch (FileNotFoundException e) {
            displayError("Database is missing.");
            return false;
        } catch (IOException e) {
            displayError("Unknown error occured during deleting the product (ID: %s) database. Please contact with the administrator.", product.getId());
            return false;
        }
    }
    
    public static boolean productExist(String id, LinkedList <Product> list) {
        for (Iterator<Product> i = list.iterator(); i.hasNext(); i.progress()) {
            if (i.getData().getId().equals(id)) return true;
        }
        return false;
    }
    
    public static Product getProduct(String id, LinkedList <Product> list) {
        for (Iterator<Product> i = list.iterator(); i.hasNext(); i.progress()) {
            if (i.getData().getId() == id) return i.getData();
        }
        return null;
    }
    
    private static boolean changeName(Product product) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter new name (keeping blank or skip commands will skip the operation)");
        try {
            String name = reader.readLine();
            if (name.length() > 0) {
                product.setName(name);
//                update(product);
                display("%s name has been updated successfully.\n", product.getName());
                return true;
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        return false;
    }
    
    private static boolean changePrice(Product product) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter new price (negative values or skip commands will skip the operation)");
        try {
            int newPrice = reader.readInt();
            if (newPrice >= 0) {
                product.setPrice(newPrice);
//                update(product);
                display("%s price has been updated successfully.\n", product.getName());
                return true;
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        return false;
    }
    
    private static boolean addStock(Product product) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter new stock value (negative integers or skip commands will skip the operation)");
        try {
            int newStock = reader.readInt();
            if (newStock >= 0) {
                product.add(newStock);
//                update(product);
                display("%s stock has been updated successfully.\n", product.getName());
                return true;
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        return false;
    }
    
    private static boolean setStock(Product product) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter new stock value (negative integers or skip commands will skip the operation)");
        try {
            int newStock = reader.readInt();
            if (newStock >= 0) {
                product.setQuantity(newStock);
//                update(product);
                display("%s stock has been updated successfully.\n", product.getName());
                return true;
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        return false;
    }
}