package com.itsabugnotafeature.securikey.constraints;

import java.util.Random;

/**
 * Created by alex on 2016/10/10.
 */

public class SpecialCharConstraint extends Constraint {

    private String specialCharset = "!\\\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    private int numSpecials = 2;


    public SpecialCharConstraint(int numSpecials) {
        this.numSpecials = numSpecials;
    }

    @Override
    public String apply(String password) throws ConstraintException {
        Random generator = new Random();
        generator.setSeed(getSeed(password));

        StringBuilder newpassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            if (generator.nextFloat() < 0.25) {
                newpassword.append(specialCharset.charAt(generator.nextInt(specialCharset.length())));
            } else {
                newpassword.append(c);
            }
        }

        System.out.println(newpassword.toString());
        return newpassword.toString();
    }

    @Override
    public boolean check(String password) throws ConstraintException {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (specialCharset.indexOf(c) != -1) {
                count += 1;
            }
        }
        return count >= numSpecials;
    }
}
