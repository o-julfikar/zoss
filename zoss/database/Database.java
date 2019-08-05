package zoss.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import zoss.io.SmartReader;
import zoss.product.Product;
import zoss.product.ProductManager;
import zoss.util.Iterator;
import zoss.util.LinkedList;
import zoss.util.LinkedListStack;
import zoss.util.SkipCommandException;
import static zoss.util.SmartCommand.undoCommand;
import zoss.util.Stack;
import zoss.util.StackOverflowException;
import zoss.util.StackUnderflowException;
import zoss.util.QuitCommandException;
import static zoss.database.Encryption.encrypt;
import static zoss.io.SmartWriter.*;

public class Database {
    public static boolean registerUser() throws QuitCommandException {
        LinkedList<User> existingUsers = loadUsers();
        String[] prompts = {"First name", "Last name", "Username", "Password", "Contact Number", "PC Level"};
        String line = "", firstName = "", lastName = "", username = "", password = "";
        int contactNumber = 0, pcLevel = 0;
        Stack <String> inputs = new LinkedListStack<String> ();
        try {
            SmartReader reader = new SmartReader(System.in);
            while (inputs.size() < prompts.length) {
                display(prompts[inputs.size()]);
                if (inputs.size() < 2) line = reader.readLine();
                else if (inputs.size() == 2) while (!undoCommand((line = reader.readUsername())) && userExist(encrypt(line), existingUsers)) displayError("User already exists with this username");
                else if (inputs.size() == 3) line = reader.readPassword();
                else if (inputs.size() == 4) while (!undoCommand(line = reader.readLine()) && !(line.matches("01[13-9][0-9]{8}"))) displayError("Please enter a valid Bangladeshi Mobile Phone number");
                else if (inputs.size() == 5) while (!undoCommand(line = reader.readLine()) && line.matches("[^1-5]")) displayError("Please enter a valid PC Level between 1 to 5 (inclusive)");
                if (undoCommand(line)) inputs.pop();
                else inputs.push(line);
            }
            pcLevel = Integer.parseInt(inputs.pop());
            contactNumber = Integer.parseInt(inputs.pop());
            password = encrypt(inputs.pop());
            username = encrypt(inputs.pop());
            lastName = inputs.pop();
            firstName = inputs.pop();
            PrintWriter usersDB = new PrintWriter(new BufferedWriter(new FileWriter(new File("zoss/database/Users.pcdb"), true)));
            User newUser = new User(username, password, firstName, lastName, contactNumber, pcLevel, 2);
            usersDB.println(newUser);
            display("%s has been registered successfully as an User.\n", newUser.getFirstName());
            usersDB.close();
            return true;
        } catch (IOException ioe) {
            displayError("Registration failed");
            return false;
        } catch (SkipCommandException sce) {
            if (existingUsers == null || existingUsers.size() == 0) {
                displayError("Skip commands are not allowed here");
                return registerUser();
            } else {
                displayError(sce.getMessage());
                return false;
            }
        } catch (StackOverflowException soe) {
            displayError("Are you a dragon? You overloaded with inputs!!! PROMOTED!!!");
            return false;
        } catch (StackUnderflowException sue) {
            return registerUser();
        }
    }
    
    public static boolean registerAdmin() throws QuitCommandException {
        LinkedList<User> existingUsers = loadUsers();
        String[] prompts = {"First name", "Last name", "Username", "Password", "Contact Number"};
        String line = "", firstName = "", lastName = "", username = "", password = "";
        int contactNumber = 0, pcLevel = 100;
        Stack <String> inputs = new LinkedListStack<String> ();
        try {
            SmartReader reader = new SmartReader(System.in);
            while (inputs.size() < prompts.length) {
                display(prompts[inputs.size()]);
                if (inputs.size() < 2) line = reader.readLine();
                else if (inputs.size() == 2) while (!undoCommand((line = reader.readUsername())) && userExist(encrypt(line), existingUsers)) displayError("User already exists with this username");
                else if (inputs.size() == 3) line = reader.readPassword();
                else if (inputs.size() == 4) while (!undoCommand(line = reader.readLine()) && !(line.matches("01[13-9][0-9]{8}"))) displayError("Please enter a valid Bangladeshi Mobile Phone number");
                else if (inputs.size() == 5) while (!undoCommand(line = reader.readLine()) && line.matches("[^1-5]")) displayError("Please enter a valid PC Level between 1 to 5 (inclusive)");
                if (undoCommand(line)) inputs.pop();
                else inputs.push(line);
            }
            contactNumber = Integer.parseInt(inputs.pop());
            password = encrypt(inputs.pop());
            username = encrypt(inputs.pop());
            lastName = inputs.pop();
            firstName = inputs.pop();
            PrintWriter usersDB = new PrintWriter(new BufferedWriter(new FileWriter(new File("zoss/database/Users.pcdb"), true)));
            User newUser = new User(username, password, firstName, lastName, contactNumber, pcLevel, 1);
            usersDB.println(newUser);
            usersDB.close();
            display("%s has been registered successfully as an Administrator.\n", newUser.getFirstName());
            return true;
        } catch (IOException ioe) {
            displayError("Registration failed");
            return false;
        } catch (SkipCommandException sce) {
            if (existingUsers == null || existingUsers.size() == 0) {
                displayError("Skip commands are not allowed here");
                return registerUser();
            } else {
                displayError(sce.getMessage());
                return false;
            }
        } catch (StackOverflowException soe) {
            displayError("Are you a dragon? You overloaded with inputs!!! PROMOTED!!!");
            return false;
        } catch (StackUnderflowException sue) {
            return registerUser();
        }
    }
    
    public static boolean updateUser(User user) {
        try {
            BufferedReader userDB = new BufferedReader(new FileReader("zoss/database/Users.pcdb"));
            StringBuffer update = new StringBuffer();
            String line;
            while ((line = userDB.readLine()) != null) {
                if (line.startsWith(user.getUsername())) update.append(user + System.lineSeparator());
                else update.append(line + System.lineSeparator());
            }
            userDB.close();
            FileOutputStream updatedDB = new FileOutputStream("zoss/database/Users.pcdb");
            updatedDB.write(update.toString().getBytes());
            updatedDB.close();
            return true;
        } catch (FileNotFoundException e) {
            displayError("Database is missing.");
            return false;
        } catch (IOException e) {
            displayError("Unknown error occured during updating the user (First Name: %s) database. Please contact with the administrator.", user.getFirstName());
            return false;
        }
    }
    
    public static boolean deleteUser(User user) {
        try {
            BufferedReader userDB = new BufferedReader(new FileReader("zoss/database/Users.pcdb"));
            StringBuffer update = new StringBuffer();
            String line;
            while ((line = userDB.readLine()) != null) {
                if (!line.startsWith(user.getUsername())) update.append(line + System.lineSeparator());
            }
            userDB.close();
            FileOutputStream updatedDB = new FileOutputStream("zoss/database/Users.pcdb");
            updatedDB.write(update.toString().getBytes());
            updatedDB.close();
            return true;
        } catch (FileNotFoundException e) {
            displayError("Database is missing.");
            return false;
        } catch (IOException e) {
            displayError("Unknown error occured during updating the user (First Name: %s) database. Please contact with the administrator.", user.getFirstName());
            return false;
        }
    }
    
    public User login() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Username");
        try {
            String username = reader.readUsername();
            display("Password");
            String password = reader.readLine();
            User currentUser = getUser(encrypt(username), encrypt(password), loadUsers(username));
            return currentUser;
        } catch (SkipCommandException e) {
            displayError("Skip commands are not allowed here");
            return login();
        }
    }
    
    public static LinkedList <User> loadUsers() {
        LinkedList <User> users = new LinkedList <User> ();
        try {
            FileInputStream loc = new FileInputStream(new File("zoss/database/Users.pcdb"));
            SmartReader reader = new SmartReader(loc);
            while (reader.hasNextLine()) {
                String[] line = reader.readLine().split(SEPARATOR);
                if (line.length != 8) continue;
                User user = new User(line);
                users.insert(user, users.size());
            }
            return users;
        } catch (FileNotFoundException e) {
            displayError("Failed to load users. Database is missing.");
        } catch (QuitCommandException e) {
            displayError("Failed to load users. Quit command invoked.");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return null;
    }
    
    public static LinkedList <User> loadUsers(String filter) {
        LinkedList <User> users = new LinkedList <User> ();
        try {
            FileInputStream loc = new FileInputStream(new File("zoss/database/Users.pcdb"));
            SmartReader reader = new SmartReader(loc);
            while (reader.hasNextLine()) {
                String[] line = reader.readLine().split(SEPARATOR);
                if (line.length != 8) continue;
                User user = new User(line);
                if (user.getUsername().equals(encrypt(filter)) || user.firstNameSimilarTo(filter) || user.lastNameSimilarTo(filter)) users.insert(user, users.size());
            }
            return users;
        } catch (FileNotFoundException e) {
            displayError("Failed to load users. Database is missing.");
        } catch (QuitCommandException e) {
            displayError("Failed to load users. Quit command invoked.");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return null;
    }
    
    public static boolean userExist(User user, LinkedList <User> list) {
        for (Iterator<User> i = list.iterator(); i.hasNext(); i.progress()) {
            if (i.getData().match(user)) return true;
        }
        return false;
    }
    
    public static boolean userExist(String username, LinkedList <User> list) {
        for (Iterator<User> i = list.iterator(); i.hasNext(); i.progress()) {
            if (i.getData().match(username)) return true;
        }
        return false;
    }
    
    public User getUser(String username, String password, LinkedList <User> list) {
        for (Iterator<User> i = list.iterator(); i.hasNext(); i.progress()) {
            if (i.getData().match(username, password)) return i.getData();
        }
        return null;
    }
    
    public static boolean addProduct() throws QuitCommandException {
        try {
            SmartReader reader = new SmartReader(System.in);
            LinkedList<Product> productList = Database.loadProducts();
            if (productList == null) throw new IOException();
            display("Product ID");
            String id;
            while ((id = reader.readProductId()).length() > 10 || ProductManager.productExist(id, productList)) displayError("Product with Product ID, %s, already exist", id);
            display("Name");
            String name = reader.readLine();
            display("Price");
            double price = reader.readDouble();
            display("Quantitiy");
            int quantity = reader.readInt();
            
            PrintWriter productsDB = new PrintWriter(new BufferedWriter(new FileWriter(new File("zoss/database/Products.pcdb"), true)));
            Product newProduct = new Product(id, name, price, quantity);
            productsDB.println(newProduct);
            productsDB.close();
            display("%s has been added to Product's List successfully.\n", newProduct.getName());
            return true;
        } catch (IOException ioe) {
            displayError("Adding product failed");
            return false;
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
    }
    
    public static LinkedList <Product> loadProducts() {
        LinkedList <Product> products = new LinkedList <Product> ();
        try {
            FileInputStream loc = new FileInputStream(new File("zoss/database/Products.pcdb"));
            SmartReader reader = new SmartReader(loc);
            while (reader.hasNextLine()) {
                String[] line = reader.readLine().split(SEPARATOR);
                if (line.length != 4) continue;
                Product product = new Product(line);
                products.insert(product, products.size());
            }
            return products;
        } catch (FileNotFoundException e) {
            displayError("Failed to load products. Database is missing.");
        } catch (QuitCommandException e) {
            displayError("Failed to load products. Quit command invoked.");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return null;
    }
    
    public static LinkedList <Product> loadProducts(String filter) {
        LinkedList <Product> products = new LinkedList <Product> ();
        try {
            FileInputStream loc = new FileInputStream(new File("zoss/database/Products.pcdb"));
            SmartReader reader = new SmartReader(loc);
            while (reader.hasNextLine()) {
                String[] line = reader.readLine().split(SEPARATOR);
                if (line.length != 4) continue;
                Product product = new Product(line);
                if (product.idSimilarTo(filter) || product.nameSimilarTo(filter)) products.insert(product, products.size());
            }
            return products;
        } catch (FileNotFoundException e) {
            displayError("Failed to load products. Database is missing.");
        } catch (QuitCommandException e) {
            displayError("Failed to load products. Quit command invoked.");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return null;
    }    
}