/**
 * Created by alex on 2016/10/08.
 */

package com.itsabugnotafeature.securikey;

import java.util.ArrayList;
import java.util.List;

import com.itsabugnotafeature.securikey.utils.TextUtil;
import com.itsabugnotafeature.securikey.utils.TextUtil.EmptyStringException;

/**
 * This class represents a password profile.
 */
public abstract class Profile {
    protected final List<Constraint> constraints;
    protected final String name;
    protected final String salt;

    public Profile(String name) throws EmptyStringException {
        this(name, name);
    }

    public Profile(String name, String salt) throws EmptyStringException {
        this(name, salt, null);
    }

    public Profile(String name, String salt, List<Constraint> constraints)
            throws EmptyStringException {

        if (!TextUtil.hasValue(name) || !TextUtil.hasValue(salt)) {
            throw new EmptyStringException("name and salt must have a value");
        }

        if (constraints == null || constraints.isEmpty()) {
            // TODO: load default constraints
            this.constraints = new ArrayList<>();
        } else {
            this.constraints = constraints;
        }

        this.name = name;
        this.salt = salt;
    }

    public String getName() {
        return this.name;
    }

    public String getSalt() {
        return this.salt;
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public List<Constraint> getConstraints() {
        return this.constraints;
    }

    /**
     * Get the hashed password this profile represents using the given master password.
     *
     * The generated password should use the profiles salt as well as its' list of constraints.
     *
     * @return the hashed master password
     */
    public abstract String getPasswordHash(String masterPassword);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;

        Profile profile = (Profile) o;

        return name.equals(profile.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}