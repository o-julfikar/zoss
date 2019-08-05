package zoss.util;

public class SmartCommand {
    public static boolean skippableCommand(String command) {
        return command.matches("skip|break|cancel|home|gohome");
    }
    
    public static boolean terminableCommand(String command) {
        return command.matches("quit|return|exit|bye|byebye|tata|seeya|seeyouagain|terminate|stop");
    }
    
    public static boolean undoCommand(String command) {
        return command.matches("undo|back|goback|prev|previous|gotoprevious|gotoprior|prior|piche|pichetodekho|oopss|ops|ooppsss|opss|oopsss|ohno|damn!|damn");
    }
}