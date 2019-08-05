package zoss.io;

import java.io.InputStream;
import java.util.Scanner;
import zoss.util.SkipCommandException;
import zoss.util.QuitCommandException;
import static zoss.io.SmartWriter.*;
import static zoss.util.SmartCommand.*;

public class SmartReader {
    Scanner sc;
    
    public SmartReader (InputStream in) {
        sc = new Scanner(in);
    }
    
    public byte readByte() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Byte.parseByte(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid byte number");
            return readByte();
        }
    }
    
    public short readShort() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Short.parseShort(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid short number");
            return readShort();
        }
    }
    
    public int readInt() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid integer number");
            return readInt();
        }
    }
    
    public long readLong() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid long number");
            return readLong();
        }
    }
    
    
    public float readFloat() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Float.parseFloat(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid float (e.g. 2.1) number");
            return readFloat();
        }
    }
    
    public double readDouble() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid double (e.g. 2.1) number");
            return readDouble();
        }
    }
    
    public char readChar() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.nextLine();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            if (line.equals("")) throw new NumberFormatException();
            return line.charAt(0);
        } catch (NumberFormatException e) {
            displayError("Please enter a valid character");
            return readChar();
        }
    }
    
    public String read() throws QuitCommandException, SkipCommandException {
        try {
            String line = sc.next();
            
            if (terminableCommand(line)) throw new QuitCommandException(line);
            else if (skippableCommand(line)) throw new SkipCommandException(line);
            
            return line;
        } catch (NumberFormatException e) {
            displayError("Please enter a valid word");
            return read();
        }
    }
    
    public String readLine() throws QuitCommandException, SkipCommandException {
        String line = sc.nextLine();
        
        if (terminableCommand(line)) throw new QuitCommandException(line);
        else if (skippableCommand(line)) throw new SkipCommandException(line);
        
        return line;
    }
    
    public String readUsername() throws QuitCommandException, SkipCommandException {
        String line = sc.nextLine();
        
        if (terminableCommand(line)) throw new QuitCommandException(line);
        else if (skippableCommand(line)) throw new SkipCommandException(line);
        
        
        String usernamePattern = "[A-Za-z]+([\\-_\\.]?[A-Za-z0-9]+)*";
        
        if (line.matches(usernamePattern)) return line;
        
        displayError("Please enter a valid username");
        return readUsername();
    }
    
    public String readPassword() throws QuitCommandException, SkipCommandException {
        String line = sc.nextLine();
        
        if (terminableCommand(line)) throw new QuitCommandException(line);
        else if (skippableCommand(line)) throw new SkipCommandException(line);
                
//        String passwordPattern = "[A-Z][a-z][0-9][ -/:-@\\[-`\\{-~]";
        if (undoCommand(line) || line.matches(".*[A-Z].*") && line.matches(".*[a-z].*") && line.matches(".*[0-9].*") && line.matches(".*[ -/:-@\\[-`\\{-~].*")) return line;
        
        displayError("Please enter a valid password more than 6 digit long, must contain a UPPERCASE letter, lowercase letter, a number and a special character");
        return readPassword();
    }
    
    public String readProductId() throws QuitCommandException, SkipCommandException {
        String line = sc.nextLine();
        
        if (terminableCommand(line)) throw new QuitCommandException(line);
        else if (skippableCommand(line)) throw new SkipCommandException(line);
        
        
        String idPattern = "[A-Za-z]+([\\-_\\.]?[A-Za-z0-9]+)*";
        
        if (line.matches(idPattern)) return line;
        
        displayError("Please enter a valid Product ID");
        return readProductId();
    }
    
    public boolean hasNextLine() {
        return sc.hasNextLine();
    }
    
    public boolean hasNext() {
        return sc.hasNext();
    }
    
    public void clear() {
        sc = new Scanner(System.in);
    }
}