package com.itsabugnotafeature.securikey.test;

import com.itsabugnotafeature.securikey.constraints.ConstraintException;
import com.itsabugnotafeature.securikey.constraints.MaxLengthConstraint;

import junit.framework.TestCase;

/**
 * Created by alex on 2016/10/10.
 */

public class TestConstraints extends TestCase {

    public void testMaxLength() {
        try {
            String password = "password";
            MaxLengthConstraint maxlength = new MaxLengthConstraint(1);
            assertEquals(maxlength.apply(password).length(), 1);
        } catch (ConstraintException exc) {
            fail();
        }

        try {
            String password = "password";
            MaxLengthConstraint maxlength = new MaxLengthConstraint(64);
            assertEquals(maxlength.apply(password).length(), 8);
        } catch (ConstraintException exc) {
            fail();
        }

        try {
            String password = "password";
            MaxLengthConstraint maxlength = new MaxLengthConstraint(0);
            assertEquals(maxlength.apply(password).length(), 0);
        } catch (ConstraintException exc) {
            fail();
        }

        try {
            String password = "password";
            MaxLengthConstraint maxlength = new MaxLengthConstraint(-1);
            maxlength.apply(password);
            fail();
        } catch (ConstraintException exc) {
            // test should fail
        }
    }

}
