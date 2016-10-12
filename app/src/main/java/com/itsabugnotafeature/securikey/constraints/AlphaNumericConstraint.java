package com.itsabugnotafeature.securikey.constraints;

/**
 * Created by alex on 2016/10/10.
 */

public class AlphaNumericConstraint extends Constraint {

    private String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private char replacement_character = 'x';

    /**
     * Ensure password is alpha-numeric only, replacing characters that do not match
     * @param replacement the character to replace out of range characters with
     */
    public AlphaNumericConstraint(char replacement) {
        replacement_character = replacement;
    }

    @Override
    public String apply(String password) throws ConstraintException {
        StringBuilder newpassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            if (charset.indexOf(c) == -1) {
                newpassword.append(replacement_character);
            } else {
                newpassword.append(c);
            }
        }
        return newpassword.toString();
    }

    @Override
    public boolean check(String password) throws ConstraintException {
        for (char c : password.toCharArray()) {
            if (charset.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }
}
