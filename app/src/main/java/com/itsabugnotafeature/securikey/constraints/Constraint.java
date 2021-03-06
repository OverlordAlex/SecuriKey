package com.itsabugnotafeature.securikey.constraints;

/**
 * Created by alex on 2016/10/08.
 */

/**
 * This class represents a constraint on an password.
 *
 * Example: alphanumeric only, or maximum length
 */
public abstract class Constraint {

    // TODO - This needs to inherit from View or something, and should be responsible for itself

    // priority determines the order in which constraints are applied
    int priority = -1;

    /**
     * Apply this constraint to the given password
     * @param password The password which should be constrained
     * @return the new password that adheres to the constraint
     * @throws ConstraintException if the password cannot satisfy the constraint
     */
    public abstract String apply(String password) throws ConstraintException;

    /**
     * Check that this constraint matches the given password
     * @param password the password to be checked
     * @return true if the password matches the constraint, false otherwise
     * @throws ConstraintException if something goes wrong
     */
    public abstract boolean check(String password) throws ConstraintException;

    protected long getSeed(String password) {
        byte[] bytes = password.getBytes();
        StringBuilder number = new StringBuilder();

        for (byte b : bytes) {
            number.append(Byte.toString(b));
        }

        return new Long(number.toString().substring(0, 10));
    }

}