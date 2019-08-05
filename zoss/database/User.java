package zoss.database;

import static zoss.io.SmartWriter.SEPARATOR;
import static zoss.util.SmartSearch.*;

public class User {
    private String firstName, lastName, username, password, status;
    private int contactNumber, pcLevel, userLevel;
    public static final int ADMIN = 1, USER = 2;
    public static final String[] userLevels = {"Beginner", "Junior", "Vice Senior", "Senior", "Expert"};
    
    public User(String username, String password, String firstName, String lastName, int contactNumber, int pcLevel, int userLevel) {
        this.username = username;
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setContactNumber(contactNumber);
        setUserLevel(userLevel);
        setPCLevel(pcLevel);
        activate();
    }
    
    public User(String[] line) {
        this.username = line[0];
        setPassword(line[1]);
        setFirstName(line[2]);
        setLastName(line[3]);
        setContactNumber(Integer.parseInt(line[4]));
        setUserLevel(Integer.parseInt(line[6]));
        setPCLevel(Integer.parseInt(line[5]));
        if (line[7].equals("Deactivated")) deactivate();
        else activate();
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public void setPCLevel(int pcLevel) {
        if (pcLevel > 0 && pcLevel < 6 || userLevel == 1) this.pcLevel = pcLevel;
    }
    
    public void setUserLevel(int userLevel) {
        if (userLevel == 1 || userLevel == 2) this.userLevel = userLevel;
    }
    
    public void activate() {
        status = "Active";
    }
    
    public void deactivate() {
        status = "Deactivated";
    }
    
    public void promote() {
        if (userLevel != 1) setPCLevel(pcLevel + 1);
    }
    
    public void demote() {
        if (userLevel != 1) setPCLevel(pcLevel - 1);
    }
    
    public String getName() {
        return getFirstName() + " " + getLastName();
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public int getContactNumber() {
        return contactNumber;
    }
    
    public int getPCLevel() {
        return pcLevel;
    }
    
    public int getUserLevel() {
        return userLevel;
    }
    
    public String getStatus() {
        return status;
    }
    
    public boolean isActive() {
        return status.equalsIgnoreCase("Active");
    }
    
    public boolean match(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
    
    public boolean match(String username) {
        return this.username.equals(username);
    }
    
    public boolean match(User other) {
        if (other == this) return true;
        
        return this.username.equals(other.username) && this.password.equals(other.password);
    }
    
    public boolean firstNameSimilarTo(String filter) {
        return getSimilarityPercentage(firstName.toLowerCase(), filter.toLowerCase()) >= 30;
    }
    
    public boolean lastNameSimilarTo(String filter) {
        return getSimilarityPercentage(lastName.toLowerCase(), filter.toLowerCase()) >= 30;
    }
    
    public String getPreferredLengthFirstName(int length) {
        if (length < 5) return ".....".substring(0, length);
        int xFactor = length * 5 / 10 + 3;
        if (firstName.length() <= length) return firstName;
        else return firstName.substring(0, length * 5 / 10) + "..." + firstName.substring(firstName.length() - (length - xFactor), firstName.length());
    }
    
    public String getPreferredLengthLastName(int length) {
        if (length < 5) return ".....".substring(0, length);
        int xFactor = length * 5 / 10 + 3;
        if (lastName.length() <= length) return lastName;
        else return lastName.substring(0, length * 5 / 10) + "..." + lastName.substring(lastName.length() - (length - xFactor), lastName.length());
    }
    
    public String toString() {
        return username + SEPARATOR + password + SEPARATOR + firstName + SEPARATOR + lastName + SEPARATOR + contactNumber + SEPARATOR + pcLevel + SEPARATOR + userLevel + SEPARATOR + status;
    }
}