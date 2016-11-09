/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

/**
 *
 * @author wouter.koppenol
 */
public class Utils {
    /**
     * Gets the string readable stack trace string.
     *
     * @param elements The elements to convert to a string.
     * @return The string.
     */
    public static String getStackTraceString(StackTraceElement[] elements) {
        return Utils.getStackTraceString(elements, System.getProperty("line.separator"));
    }

    /**
     * Gets the string readable stack trace string.
     *
     * @param elements The elements to convert to a string.
     * @return The string.
     */
    public static String getStackTraceString(StackTraceElement[] elements, String lineSeparator) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : elements) {
            sb.append(ste.toString()).append(lineSeparator);
        }
        return sb.toString();
    }
}
