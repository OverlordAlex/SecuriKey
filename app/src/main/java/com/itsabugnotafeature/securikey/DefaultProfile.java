/**
 * Created by alex on 2016/10/08.
 */

package com.itsabugnotafeature.securikey;

import com.itsabugnotafeature.securikey.crypto.Crypto;
import com.itsabugnotafeature.securikey.utils.TextUtil.EmptyStringException;

import java.util.List;

public class DefaultProfile extends Profile {

    public DefaultProfile(String name) throws EmptyStringException {
        super(name, name);
    }

    public DefaultProfile(String name, String salt) throws EmptyStringException {
        super(name, salt);
    }

    public DefaultProfile(String name, String salt, List<Constraint> constraints)
            throws EmptyStringException {
        super(name, salt, constraints);
    }

    @Override
    public String getPasswordHash(String masterPassword) {
        // TODO - actually implement the hash

        return Crypto.md5(masterPassword + this.salt);
    }
}