package zoss.programmerscafe;

import java.util.NoSuchElementException;
import zoss.database.Database;
import zoss.database.User;
import zoss.io.SmartReader;
import zoss.util.QuitCommandException;
import zoss.util.YesNoDialog;
import static zoss.io.SmartWriter.*;
import static zoss.programmerscafe.SmartMenu.*;

public class ProgrammersCafe {
    public static int terminatedDueToInputStreamClosed = 0;
    
    public static void main(String[] args) {
        try {
            while (true) {
                displayBanner(); displayBlank();
                if (firstUse()) {
                    openRegistrationForm(User.ADMIN);
                } else {
                    User user = openLoginForm();
                    if (user != null) {
                        if (!user.isActive()) {
                            if (user.getUserLevel() == 1) {
                                SmartReader reader = new SmartReader(System.in);
                                display("Dear %s, your account is deactivated. Do you want to activate you account?\n\t1. Yes\n\t2. No", user.getName());
                                int choice;
                                while((choice = reader.readInt()) < 1 || choice > 2) displayError("Please choose a valid option from the above list (1 or 2)");
                                if (choice == 1) {
                                    user.activate();
                                    Database.updateUser(user);
                                    display("Your account is activated successfully. Welcome back!");
                                } else {
                                    display("See you on board soon ;) Enjoy the vacation!");
                                }
                            } else {
                                displayError("Dear %s, your account is deactivated. Please contact with an administrator to reactivate your account.", user.getName());
                            }
                            displayBlank();
                        }
                        
                        if (user.isActive() && user.getUserLevel() == 1) {
                            displayAdminMenu(user);
                        } else if (user.isActive() && user.getUserLevel() == 2) {
                            displayUserMenu(user);
                        }
                        
                    } else {
                        displayError("Username and password did not match!\n");
                    }
                }
            }
        } catch (NoSuchElementException e) {
            displayError("The input stream is closed. The program is termitted.\n");
            if (terminatedDueToInputStreamClosed >= 5) System.exit(0);
            terminatedDueToInputStreamClosed++;
        } catch (QuitCommandException e) {
            if (!e.safe()) displayError("%s. The program is termitted.\n", e.getMessage());
        } catch (Exception e) {
            display("An unknown error occured. Please contact with the developer with the code:\n");
            e.printStackTrace();
            displayError("\nSorry for the inconvenience, we are working hard to make your experience smoother in every update. Keep supporting us.\n\nPlease login again.");
        } finally {
            display("Thank you for using our service.");
        }
    }
}