package zoss.user;

import zoss.database.Database;
import zoss.database.User;
import zoss.io.SmartReader;
import zoss.util.LinkedList;
import zoss.util.SkipCommandException;
import zoss.util.QuitCommandException;
import static zoss.database.Encryption.encrypt;
import static zoss.io.SmartWriter.*;

public class UserManager {
    public void showAllUsers() {
        LinkedList<User> users = Database.loadUsers();
        if (users.size() == 0) {
            display("<< No users found >>");
        } else {
            display(" %-7s  %-22s  %-10s  %-15s  %-8s  %-10s", "SN", "Fist Name", "Last Name", "Contact Number", "PC Level", "Status");
            for (int i = 0; i < users.size(); i++) {
                display("#%-7d  %-22s  %-10s  0%-14s  %-8s  %-10s", i + 1, users.get(i).getPreferredLengthFirstName(22), users.get(i).getPreferredLengthLastName(10), users.get(i).getContactNumber(), users.get(i).getPCLevel(), users.get(i).getStatus());
            }
        }
        displayBlank();
    }
    
    public void showFilteredUsers() throws QuitCommandException {
        try {
            SmartReader reader = new SmartReader(System.in);
            display("Enter Username/ Full name/ Last Name");
            String filter = reader.readLine();
            LinkedList<User> users = Database.loadUsers(filter);
            if (users.size() == 0) {
                display("<< No users found >>");
            } else {
                display(" %-7s  %-22s  %-10s  %-15s  %-8s  %-10s", "SN", "Fist Name", "Last Name", "Contact Number", "PC Level", "Status");
                for (int i = 0; i < users.size(); i++) {
                    display("#%-7d  %-22s  %-10s  0%-14s  %-8s  %-10s", i + 1, users.get(i).getPreferredLengthFirstName(22), users.get(i).getPreferredLengthLastName(10), users.get(i).getContactNumber(), users.get(i).getPCLevel(), users.get(i).getStatus());
                }
            }
        } catch (SkipCommandException e) {
            displayError(e.getMessage());
        }
        displayBlank();
    }    
    
    public void editUser() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter Username/ Full name/ Last Name");
        try {
            String filter = reader.readLine();
            LinkedList<User> users = Database.loadUsers(filter);
            if (users.size() == 0) {
                displayError("<< No users found >>");
            } else {
//            display(" %-7s  %-22s  %-10s  %-10s", "SN", "Fist Name", "Last Name", "Status");
                display("Please choose an user from the list below:\n");
                for (int i = 0; i < users.size(); i++) {
                    display("#%-7d  %-22s  %-10s  %-10s", i + 1, users.get(i).getPreferredLengthFirstName(22), users.get(i).getPreferredLengthLastName(10), users.get(i).getStatus());
                }
                int choice;
                while ((choice = reader.readInt()) < 1 || choice > users.size()) displayError("Please choose a valid index from 1 to %d", users.size());
                User selectedUser = users.get(choice - 1);
                while (true) {
                    display("Selected user: %s\n", selectedUser.getFirstName());
                    display("Choose an option from the below:\n\n" + 
                            "\t1. Edit First Name\n" +
                            "\t2. Edit Last Name\n" +
                            "\t3. Edit Contact Number\n" +
                            "\t4. Promote\n" +
                            "\t5. Demote\n" +
                            "\t6. %s\n" +
                            "\t7. Reset Password\n" +
                            "\t8. Cancel\n" +
                            "\t0. Save Changes\n", selectedUser.isActive() ? "Deactivate" : "Activate");
                    int selectedOption;
                    while ((selectedOption = reader.readInt()) < 0 || selectedOption > 8) displayError("Please choose a valid index from 0 to 8");
                    String firstName, lastName;
                    int contactNumber;
                    switch(selectedOption) {
                        case 1:
                            display("Enter first name (Existing: %s, leave blank to skip)", selectedUser.getFirstName());
                            firstName = reader.readLine();
                            if (firstName.length() > 0) {
                                selectedUser.setFirstName(firstName);
                                display("First name has been changed to '%s' successfully.\n", firstName);
                            }
                            break;
                        case 2:
                            display("Enter last name (Existing: %s, leave blank to skip)", selectedUser.getLastName());
                            lastName = reader.readLine();
                            if (lastName.length() > 0) {
                                selectedUser.setLastName(lastName);
                                display("Last name has been changed to '%s' successfully.\n", lastName);
                            }
                            break;
                        case 3:
                            display("Enter contact number (Existing: %d, enter 0 or less to skip)", selectedUser.getContactNumber());
                            while (!String.valueOf(contactNumber = reader.readInt()).matches("1[13-9][0-9]{8}")) displayError("Please enter a valid Bangladeshi Mobile Phone number");;
                            if (contactNumber > 0) selectedUser.setContactNumber(contactNumber);
                            break;
                        case 4:
                            selectedUser.promote();
                            display("%s promoted to %d PC Level\n", selectedUser.getFirstName(), selectedUser.getPCLevel());
                            break;
                        case 5:
                            selectedUser.demote();
                            display("%s demoted to %d PC Level\n", selectedUser.getFirstName(), selectedUser.getPCLevel());
                            break;
                        case 6:
                            if (selectedUser.isActive()) selectedUser.deactivate();
                            else selectedUser.activate();
                            display("%s's account is now %s\n", selectedUser.getFirstName(), selectedUser.getStatus());
                            break;
                        case 7:
                            selectedUser.setPassword(encrypt("recovered"));
                            display("%s's password has been reset. Current passoword: recovered.\n", selectedUser.getName());
                            break;
                        case 8:
                            display("Are you sure you want to cancel? All unsaved data will be lost permanently.\n\t1. Yes\n\t2. No");
                            int selectedChoice;
                            while((selectedChoice = reader.readInt()) < 1 || selectedChoice > 2) displayError("Please choose a valid option from the above list (1 - 2)");
                            if (selectedChoice == 2) break;
                            display("All changes has been restored.\n");
                            return;
                        case 0:
                            if (Database.updateUser(selectedUser)) display("All changes are saved.");
                            else displayError("Failed to save changes. All changes has been restored.");
                            displayBlank();
                            return;
                        default:
                            displayError("Please choose a valid option from the above list.\n");
                            break;
                    }
                }
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        displayBlank();
    }
    
    public void resetUserPassword() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter Username/ Full name/ Last Name");
        try {
            String filter = reader.readLine();
            LinkedList<User> users = Database.loadUsers(filter);
            if (users.size() == 0) {
                displayError("<< No users found >>");
            } else {
                display("Please choose an user from the list below:\n");
                for (int i = 0; i < users.size(); i++) {
                    display("#%-5d %-22s %-10s %-10s", i + 1, users.get(i).getPreferredLengthFirstName(22), users.get(i).getPreferredLengthLastName(10), users.get(i).getStatus());
                }
                int choice;
                while ((choice = reader.readInt()) < 1 || choice > users.size()) displayError("Please choose a valid index from 1 to %d", users.size());
                User selectedUser = users.get(choice - 1);
                selectedUser.setPassword(encrypt("recovered"));
                if (Database.updateUser(selectedUser)) display("%s's password has been reset. Current passoword: recovered.\n", selectedUser.getName());            
                else displayError("Failed to save reset password.");
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        displayBlank();
    }
    
    // Outdated
    public boolean deleteUser() throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Enter Username/ Full name/ Last Name");
        try {
            String filter = reader.readLine();
            LinkedList<User> users = Database.loadUsers(filter);
            if (users.size() == 0) {
                displayError("<< No users found >>");
            } else {
                display("Please choose an user from the list below:\n");
                for (int i = 0; i < users.size(); i++) {
                    display("#%-5d %-22s %-10s %-10s", i + 1, users.get(i).getPreferredLengthFirstName(22), users.get(i).getPreferredLengthLastName(10), users.get(i).getStatus());
                }
                int choice;
                while ((choice = reader.readInt()) < 1 || choice > users.size()) displayError("Please choose a valid index from 1 to %d", users.size());
                User selectedUser = users.get(choice - 1);
                return delete(selectedUser);
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        displayBlank();
        return false;
    }
    
    public static boolean deactivate(User user) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        display("Do you really want to deactivate your account?\n\t1. Yes\n\t2. No");
        int selectedChoice;
        try{
            while((selectedChoice = reader.readInt()) < 1 || selectedChoice > 2) displayError("Please choose a valid option from the above list (1 - 2)");
            if (selectedChoice == 1) {
                user.deactivate();
                if (Database.updateUser(user)) {
                    display("Account deactivated successfully.\n");
                    return true;
                } else {
                    displayError("Failed to deactivate account.\n");
                    return false;
                }
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        }
        return false;
    }
    
    public static boolean delete(User user) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        
        display("Are you sure you want to delete this account permanently?\n\t1. Yes\n\t2. No");
        int selectedOption;
        try {
            while ((selectedOption = reader.readInt()) < 1 || selectedOption > 2) displayError("Please choose either 1 (for Yes) or 2 (for No)");
            if (selectedOption == 1) {
                if (Database.deleteUser(user)) {
                    display("%s's account has been successfully removed permanently.\n", user.getFirstName());
                    return true;
                } else {
                    displayError("Failed to delete the account.\n");
                    return false;
                }
            }
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return false;
    }
    
    public static boolean changePassword(User user) throws QuitCommandException {
        SmartReader reader = new SmartReader(System.in);
        
        display("Enter old password");
        String oldPassword;
        try {
            while(!(oldPassword = encrypt(reader.readPassword())).equals(user.getPassword())) displayError("Incorrect password");
            String newPassword = oldPassword, confirmPassword = oldPassword;
            display("Enter new password");
            newPassword = reader.readPassword();
            display("Confirm password");
            while (!newPassword.equals(confirmPassword  = reader.readPassword())) displayError("Password did not match");
            user.setPassword(encrypt(confirmPassword));
            if (Database.updateUser(user)) display("Password has been changed successfully.\n", user.getFirstName());
            else displayError("Failed to change password.\n");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
        }
        return false;
    }
    
    public static String getLevelName(int level) {
        return User.userLevels[level - 1];
    }
}