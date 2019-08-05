package zoss.io;

public class SmartWriter {
    public final static String SEPARATOR = String.valueOf((char) 28);
    public final static String NEWLINE = System.lineSeparator();
    
    public static void display(Object format, Object... obj) {
        System.out.printf(format + "\n", obj);
    }
    
    public static void displayBlank() {
        System.out.println();
    }
    
    public static void displayError(Object format, Object... obj) {
        System.err.printf(format + "\n", obj);
    }
    
    public static void displayBanner() {
        System.out.print("################################################################################\n" +
             "################################################################################\n" + 
             "#####################                                      #####################\n" + 
             "#########                      Programmer's Cafe!                      #########\n" + 
             "###                        ");
        System.err.print("Eat - Drink - Code - Enjoy"); 
        display("                        ###");
    }
}