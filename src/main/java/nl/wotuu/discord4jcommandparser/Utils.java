/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

import java.util.Collection;
import java.util.Iterator;

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
    
    /**
     * Join a string array or list with a delimiter.
     *
     * @param s         The array or list to join.
     * @param delimiter The delimiter to glue the pieces together with.
     * @return The joined string.
     */
    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = s.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (!iterator.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
