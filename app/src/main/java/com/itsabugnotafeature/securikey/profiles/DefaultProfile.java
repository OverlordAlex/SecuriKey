/**
 * Created by alex on 2016/10/08.
 */

package com.itsabugnotafeature.securikey.profiles;

import com.itsabugnotafeature.securikey.constraints.Constraint;
import com.itsabugnotafeature.securikey.constraints.ConstraintException;
import com.itsabugnotafeature.securikey.constraints.MaxLengthConstraint;
import com.itsabugnotafeature.securikey.constraints.MinLengthConstraint;
import com.itsabugnotafeature.securikey.constraints.SpecialCharConstraint;
import com.itsabugnotafeature.securikey.constraints.UppercaseConstraint;
import com.itsabugnotafeature.securikey.crypto.Crypto;
import com.itsabugnotafeature.securikey.utils.TextUtil.EmptyStringException;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;

public class DefaultProfile extends Profile {

    public DefaultProfile(String name) throws EmptyStringException {
        super(name, name);

        addConstraint(new UppercaseConstraint(2));
        addConstraint(new SpecialCharConstraint(2));
        addConstraint(new MinLengthConstraint(8));
        addConstraint(new MaxLengthConstraint(16));
    }

    public DefaultProfile(String name, String salt) throws EmptyStringException {
        super(name, salt);

        addConstraint(new UppercaseConstraint(2));
        addConstraint(new SpecialCharConstraint(2));
        addConstraint(new MinLengthConstraint(8));
        addConstraint(new MaxLengthConstraint(16));
    }

    public DefaultProfile(String name, String salt, List<Constraint> constraints)
            throws EmptyStringException {
        super(name, salt, constraints);
    }

    @Override
    public String getPasswordHash(String masterPassword) {
        // TODO - move this into the superclass
        int max_attempts = 50;
        int attempts = 0;

        do {
            attempts += 1;

            try {
                // TODO - actually implement the hash/salt
                String hash = Crypto.getStrongHash(masterPassword, this.salt+ Integer.toString(attempts));

                // TODO sort constraints by priority
                for (Constraint constraint : this.constraints) {
                    hash = constraint.apply(hash);
                }

                boolean failedConstraint = false;
                for (Constraint constraint : this.constraints) {
                    if (!constraint.check(hash)) {
                        failedConstraint = true;
                        break;
                    };
                }
                if (failedConstraint) {
                    continue;
                }

                return hash;

            } catch (ConstraintException exc) {
                // log something
            }

        } while (attempts < max_attempts);

        // TODO throw new exception
        return "COULD NOT GENERATE PASSWORD";
    }
}