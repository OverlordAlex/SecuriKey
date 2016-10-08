package com.itsabugnotafeature.securikey;

import java.util.ArrayList;

/**
 * Created by alex on 2016/10/08.
 */

// TODO - load the default constraints from settings

/**
 * This class represents a password profile.
 */
public abstract class Profile {
    ArrayList<Constraint> constraints = new ArrayList<>();
    String name = "";
    String salt = name;

    public Profile() {
        this.name = "DefaultProfile";
        this.salt = "DefaultProfile";
    }

    public Profile(String name, String salt) {
        this.name = name;
        this.salt = salt;
    }

    public Profile(String name) {
        this(name, name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return this.salt;
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public ArrayList<Constraint> getConstraints() {
        return this.constraints;
    }

    /**
     * Get the hashed password this profile represents using the given master password.
     *
     * The generated password should use the profiles salt as well as its' list of constraints.
     *
     * @return the hashed master password
     */
    public abstract String getHash(String masterPassword);

}
