/**
 * Created by alex on 2016/10/08.
 */

package com.itsabugnotafeature.securikey.profiles;

import com.itsabugnotafeature.securikey.constraints.Constraint;
import com.itsabugnotafeature.securikey.constraints.ConstraintException;
import com.itsabugnotafeature.securikey.constraints.MaxLengthConstraint;
import com.itsabugnotafeature.securikey.crypto.Crypto;
import com.itsabugnotafeature.securikey.utils.TextUtil.EmptyStringException;

import java.util.List;

public class DefaultProfile extends Profile {

    public DefaultProfile(String name) throws EmptyStringException {
        super(name, name);
        addConstraint(new MaxLengthConstraint(8));
    }

    public DefaultProfile(String name, String salt) throws EmptyStringException {
        super(name, salt);
        addConstraint(new MaxLengthConstraint(8));
    }

    public DefaultProfile(String name, String salt, List<Constraint> constraints)
            throws EmptyStringException {
        super(name, salt, constraints);
    }

    @Override
    public String getPasswordHash(String masterPassword) {
        int max_attempts = 5;
        int attempts = 1;

        do {
            try {
                // TODO - actually implement the hash/salt
                String hash = Crypto.md5(masterPassword, this.salt);
                String hash2 = Crypto.getStrongHash(masterPassword, this.salt);

                for (Constraint constraint : this.constraints) {
                    hash = constraint.apply(hash);
                }

                return hash2;

            } catch (ConstraintException exc) {
                // log something
            }

            attempts += 1;
        } while (attempts < max_attempts);

        // TODO throw new exception
        return "COULD NOT GENERATE PASSWORD";
    }
}