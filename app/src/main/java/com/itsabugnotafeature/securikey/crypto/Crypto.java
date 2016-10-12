package com.itsabugnotafeature.securikey.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alex on 2016/10/08.
 */


/**
 * TODO - alhorithms return a buyte array,
 *                  this provides much flexibility but we need to be smart about generating them
 *
 */

public class Crypto {

    final static String DEFAULT_ALGORITHM = "SHA-256";
    final static Integer DEFAULT_ITERATIONS = 1;

    public static final String getBasicHash(String s) {
        return getBasicHash(s, DEFAULT_ALGORITHM);
    }

    public static final String getBasicHash(String s, String algorithm) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // TODO
            e.printStackTrace();
        }
        return "";
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        return getBasicHash(s, MD5);
    }

    public static final String SHA1(final String s) {
        final String SHA1 = "SHA-1";
        return getBasicHash(s, SHA1);
    }

    public static final String SHA256(final String s) {
        final String SHA256 = "SHA-256";
        return getBasicHash(s, SHA256);
    }


    public static final String SHA512(final String s) {
        final String SHA512 = "SHA-512";
        return getBasicHash(s, SHA512);
    }



}
