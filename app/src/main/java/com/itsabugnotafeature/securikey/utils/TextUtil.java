/**
 * Created by Arne on 10/8/2016.
 */

package com.itsabugnotafeature.securikey.utils;

public class TextUtil {

    public static boolean hasValue(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return true;
    }

    public static class EmptyStringException extends Exception {

        public EmptyStringException(String message) {
            super(message);
        }
    }
}
