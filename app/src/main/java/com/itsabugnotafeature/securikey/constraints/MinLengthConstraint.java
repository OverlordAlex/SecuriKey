package com.itsabugnotafeature.securikey.constraints;

/**
 * Created by alex on 2016/10/10.
 */

public class MinLengthConstraint extends Constraint {

    private int minLength = 32;

    public MinLengthConstraint(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public String apply(String password) throws ConstraintException {
        // TODO - do we want to pad it or something?
        //        its probably better to just get a new one
        if (password.length() < minLength) {
            throw new ConstraintException();
        }
        return password;
    }

    @Override
    public boolean check(String password) throws ConstraintException {
        return password.length() >= minLength;
    }
}
