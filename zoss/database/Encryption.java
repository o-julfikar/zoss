package zoss.database;

public class Encryption {
    public static String encrypt(String source) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890".toCharArray();
        StringBuilder cypher = new StringBuilder();
        for (int i = 0; i < source.length(); i++) cypher.append(chars[source.codePointAt(i) * 24 / 2 % chars.length]++);
        return cypher.toString();
    }
}