package com.itsabugnotafeature.securikey.constraints;

import java.util.Random;

/**
 * Created by alex on 2016/10/10.
 */

public class UppercaseConstraint extends Constraint {

    private String upperCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String lowerCharset = "abcdefghijklmnopqrstuvwxyz";
    private int minNumberOfUpper = 8;

    public UppercaseConstraint(int minUppers) {
        minNumberOfUpper = minUppers;
    }

    private int countUppers(String toCount) {
        int count = 0;
        for (char c : toCount.toCharArray()) {
            if (upperCharset.indexOf(c) != -1) {
                count += 1;
            }
        }
        return count;
    }

    @Override
    public String apply(String password) throws ConstraintException {
        Random generator = new Random();
        generator.setSeed(getSeed(password));

        int iterations = 0;
        int max_iterations = 10;
        int count = 0;

        // TODO do-while maybe?
        while (iterations < max_iterations) {
            count = countUppers(password);
            if (count > minNumberOfUpper) {
                break;
            }

            StringBuilder newpassword = new StringBuilder();
            for (char c : password.toCharArray()) {
                int index = lowerCharset.indexOf(c);
                if (index != -1) {
                    if (generator.nextBoolean()) {
                        newpassword.append(upperCharset.charAt(index));
                    } else {
                        newpassword.append(c);
                    }
                } else {
                    newpassword.append(c);
                }
            }

            password = newpassword.toString();
            iterations += 1;
        }

        return password;
    }

    @Override
    public boolean check(String password) throws ConstraintException {
        int count = countUppers(password);

        if (count < minNumberOfUpper) {
            return false;
        }

        return true;
    }
}
