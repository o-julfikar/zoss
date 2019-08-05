package zoss.util;

public class SkipCommandException extends Exception {
    
    final static long serialVersionUID = 100;
    
    public SkipCommandException (String msg) {
        super("Operation skipped by skip command '" + msg + "'");
    }
}