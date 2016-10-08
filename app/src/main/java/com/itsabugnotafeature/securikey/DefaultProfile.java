package com.itsabugnotafeature.securikey;


/**
 * Created by alex on 2016/10/08.
 */

public class DefaultProfile extends Profile {

    String name = "DefaultProfile";

    public DefaultProfile() {
        super();
    }

    public DefaultProfile(String name, String salt) {
        super(name, salt);
    }

    public DefaultProfile(String name) {
        super(name, name);
    }

    @Override
    public String getHash(String masterPassword) {
        // TODO - actually implement the hash

        return Crypto.md5(masterPassword + this.salt);
    }


}
