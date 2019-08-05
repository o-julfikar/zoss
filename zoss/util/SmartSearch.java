package zoss.util;

public class SmartSearch {
    public static int getSimilarityPercentage(String source, String filter) {
        if (filter.length() == 0) return 100;
        int maxSuccessive = 0, successive = 0;
        for (int i = 0; i < source.length() && successive < filter.length(); i++) {
            if (successive > 0) {
                if (source.charAt(i) == filter.charAt(successive)) successive++;
                else {
                    maxSuccessive = Math.max(maxSuccessive, successive);
                    successive = 0;
                }
            } if (source.charAt(i) == filter.charAt(0)) successive++;
        }
        
        maxSuccessive = Math.max(maxSuccessive, successive);
        
        return 100 * maxSuccessive / filter.length();
    }
}