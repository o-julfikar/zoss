package zoss.util;

import zoss.io.SmartReader;
import static zoss.io.SmartWriter.*;
public class YesNoDialog {
    public static final boolean YES_RESPONSE = true, NO_RESPONSE = false;
    
    public static boolean show(String message) {
        display("%s\n\t1. Yes\n\t2. No", message);
        int choice;
        SmartReader reader = new SmartReader(System.in);
        try {
            while((choice = reader.readInt()) < 1 || choice > 2) displayError("Please select a valid option fromt the above list");
        } catch (SkipCommandException sce) {
            displayError(sce.getMessage());
            return false;
        } catch (QuitCommandException qce) {
            displayError("Quit commands are not allowed here");
            return show(message);
        }
        
        if (choice == 1) return true;
        else return false;
    }
}