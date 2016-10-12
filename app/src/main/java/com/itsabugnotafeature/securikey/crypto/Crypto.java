package com.itsabugnotafeature.securikey.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by alex on 2016/10/08.
 */


/**
 * TODO - alhorithms return a byte array,
 *                  this provides much flexibility but we need to be smart about generating them
 * TODO - Transform the salt?
 */

public class Crypto {

    final static String DEFAULT_ALGORITHM = "SHA-256";
    final static Integer DEFAULT_ITERATIONS = 1000;
    final static Integer DEFAULT_LENGTH = 64 * 16;

    private static final String toHex(byte[] input) {
        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte piece : input) {
            String h = Integer.toHexString(0xFF & piece);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }

    public static final String getBasicHash(String pwd, String salt) {
        return getBasicHash(pwd, salt, DEFAULT_ALGORITHM);
    }

    public static final String getBasicHash(String pwd, String salt, String algorithm) {
        try {
            // Create Hash
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(salt.getBytes());

            byte messageDigest[] = digest.digest(pwd.getBytes());
            return toHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            // TODO
            e.printStackTrace();
        }
        return "";
    }

    public static final String md5(final String s, final String salt) {
        final String MD5 = "MD5";
        return getBasicHash(s, salt, MD5);
    }

    public static final String SHA1(final String s, final String salt) {
        final String SHA1 = "SHA-1";
        return getBasicHash(s, salt, SHA1);
    }

    public static final String SHA256(final String s, final String salt) {
        final String SHA256 = "SHA-256";
        return getBasicHash(s, salt, SHA256);
    }

    public static final String SHA512(final String s, final String salt) {
        final String SHA512 = "SHA-512";
        return getBasicHash(s, salt, SHA512);
    }

    public static String getStrongHash(String password, String salt) {
        return getStrongHash(password, salt, DEFAULT_ITERATIONS, DEFAULT_LENGTH);
    }

    public static String getStrongHash(String password, String salt, Integer iterations, Integer length) {
        try {
            char[] chars = password.toCharArray();
            byte[] salt_bytes = salt.getBytes();

            PBEKeySpec spec = new PBEKeySpec(chars, salt_bytes, iterations, length);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return toHex(hash);
        } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
            // TODO
            e.printStackTrace();
        }
        return "";
    }


}
