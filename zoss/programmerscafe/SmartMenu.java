package zoss.programmerscafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import zoss.database.Database;
import zoss.database.User;
import zoss.io.SmartReader;
import zoss.order.OrderManager;
import zoss.product.ProductManager;
import zoss.user.UserManager;
import zoss.util.SkipCommandException;
import zoss.util.QuitCommandException;
import static zoss.io.SmartWriter.*;

public class SmartMenu {
    public static boolean openRegistrationForm(int control) throws QuitCommandException {
        display("Register\n");
        if (control == User.ADMIN) return Database.registerAdmin();
        else return Database.registerUser();
    }
    
    public static User openLoginForm() throws QuitCommandException {
        display("Login\n");
        Database db = new Database();
        return db.login();
    }
    
    public static boolean firstUse() {
        try {
            FileInputStream loc = new FileInputStream(new File("zoss/database/Users.pcdb"));
            SmartReader reader = new SmartReader(loc);
            while (reader.hasNextLine()) return false;
        } catch (FileNotFoundException e) {
            displayError("Failed to load users. Database is missing.");
        }
        return true;
    }
    
    public static void displayAdminMenu(User user) throws QuitCommandException{
        display("Admin Panel: Logged in as '%s'\n", user.getName());
        while (true) {
            display("Please choose an option:\n\n" + 
                    "\t1. Add Admin\n" + 
                    "\t2. Add User\n" +
                    "\t3. Manage Products\n" +
                    "\t4. Manage Users\n" +
                    "\t5. Deactivate Account\n" +
                    "\t6. Delete Account\n" +
                    "\t7. Change Password\n" +
                    "\t8. Sign Out\n" +
                    "\t0. Quit\n");
            SmartReader reader = new SmartReader(System.in);
            try {
                int choice = reader.readInt();
                
                if (choice == 0) {
                    if (OrderManager.numberOfPendingOrders() != 0) {
                        displayError("There are %d pending orders. Quitting may result in canceling the orders permanently", OrderManager.numberOfPendingOrders());
                        displayError("Are you sure to Quit?\n\t1. Yes\n\t2. No");
                        switch(reader.readInt()) {
                            case 1: throw new QuitCommandException("safe");
                        }
                    } else {
                        throw new QuitCommandException("safe");
                    }
                } else if (choice == 1) Database.registerAdmin();
                else if (choice == 2) Database.registerUser();
                else if (choice == 3) displayManageProductsMenu();
                else if (choice == 4) displayManageUsersMenu();
                else if (choice == 5) UserManager.deactivate(user);
                else if (choice == 6) UserManager.delete(user);
                else if (choice == 7) UserManager.changePassword(user);
                else if (choice == 8) return;
                else displayError("Please choose a valid option from the above list (0 to 8)\n");
            } catch (SkipCommandException e) {
                displayError("Skip commands are not allowed here.\n");
            }
        }
    }
    
    public static void displayUserMenu(User user) throws QuitCommandException {
        display("User Panel: Logged in as '%s'", user.getName());
        displayError("%s\n", UserManager.getLevelName(user.getPCLevel()));
        while (true) {
            display("Please choose an option:\n\n" + 
                    "\t1. Add Order\n" + 
                    "\t2. Serve\n" +
                    "\t3. View Pending Orders (%d Pending)\n" +
                    "\t4. Undo Last Served Order (%d Served)\n" +
                    "\t5. View Next Order\n" +
                    "\t6. View Last Order\n" +
                    "\t7. Change Password\n" +
                    "\t8. Sign Out\n" +
                    "\t0. Quit\n", OrderManager.numberOfPendingOrders(), OrderManager.numberOfServedOrders());
            
            SmartReader reader = new SmartReader(System.in);
            try {
                int choice = reader.readInt();
                if (choice == 1) OrderManager.addOrder();
                else if (choice == 2) OrderManager.serve();
                else if (choice == 3) OrderManager.showOrders();
                else if (choice == 4) OrderManager.undoLastServed();
                else if (choice == 5) OrderManager.peekNextOrder();
                else if (choice == 6) OrderManager.peekLastOrder();
                else if (choice == 7) UserManager.changePassword(user);
                else if (choice == 8) break;
                else if (choice == 0) {
                    if (OrderManager.numberOfPendingOrders() != 0) {
                        displayError("There are %d pending orders. Quitting will result in canceling the orders permanently", OrderManager.numberOfPendingOrders());
                        displayError("Are you sure to Quit?\n\t1. Yes\n\t2. No");
                        switch(reader.readInt()) {
                            case 1: throw new QuitCommandException("safe");
                        }
                    } else {
                        throw new QuitCommandException("safe");
                    }
                }
                else displayError("Please choose a valid option from the above list (0 to 8)\n");
            } catch (SkipCommandException e) {
                displayError(e.getMessage());
                displayUserMenu(user);
                break;
            }
        }
    }
    
    public static void displayManageProductsMenu() throws QuitCommandException {
        display("Please choose an option:\n\n" + 
                "\t1. Add a product\n" + 
                "\t2. Edit a product\n" + 
                "\t3. Show all products\n" + 
                "\t4. Browse products\n" + 
                "\t5. Delete a product\n" + 
                "\t0. Close\n");
        
        SmartReader reader = new SmartReader(System.in);
        try {
            int choice = reader.readInt();
            if (choice == 1) Database.addProduct();
            else if (choice == 2) ProductManager.editProduct();
            else if (choice == 3) ProductManager.showAllProducts();
            else if (choice == 4) ProductManager.showFilteredProducts();
            else if (choice == 5) ProductManager.deleteProduct();
            else if (choice == 0) return;
            else displayError("Please choose a valid option from the above list\n");
        } catch (SkipCommandException e) {
            displayError("Skip commands are not allowed here");
        }
        displayManageProductsMenu();
    }
    
    public static void displayManageUsersMenu() throws QuitCommandException {
        display("Please choose an option:\n\n" + 
                "\t1. Show all users\n" + 
                "\t2. Browse users\n" +
                "\t3. Edit a user\n" + 
                "\t4. Reset user password\n" + 
                "\t5. Delete a user\n" + 
                "\t0. Close\n");
        
        SmartReader reader = new SmartReader(System.in);
        try {
            int choice = reader.readInt();
            UserManager usersManager = new UserManager();
            if (choice == 1) usersManager.showAllUsers();
            else if (choice == 2) usersManager.showFilteredUsers();
            else if (choice == 3) usersManager.editUser();
            else if (choice == 4) usersManager.resetUserPassword();
            else if (choice == 5) usersManager.deleteUser();
            else if (choice == 0) return;
            else displayError("Please choose a valid option from the above list\n");
            
        } catch (SkipCommandException e) {
            displayError("Skip commands are not allowed here");
        }
        displayManageUsersMenu();
    }
}