package zoss.util;

public class QuitCommandException extends Exception {
    
    final static long serialVersionUID = 100;
    
    String msg;
    
    public QuitCommandException(String msg) {
        super("Quit command '" + msg + "' invoked");
        this.msg = msg;
    }
    
    public boolean safe() {
        return msg.trim().equalsIgnoreCase("safe");
    }
}