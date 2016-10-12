package com.itsabugnotafeature.securikey.constraints;

/**
 * Created by alex on 2016/10/10.
 */

public class MaxLengthConstraint extends Constraint {

    private int maxLength = 128;

    public MaxLengthConstraint(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String apply(String password) throws ConstraintException {
        try {
            if (password != null) {
                password = password.substring(0, Math.min(password.length(), maxLength));
            }

            return password;
        } catch (Exception ex) {
            throw new ConstraintException();
        }

    }

    @Override
    public boolean check(String password) throws ConstraintException {
        return password.length() <= maxLength;
    }
}
