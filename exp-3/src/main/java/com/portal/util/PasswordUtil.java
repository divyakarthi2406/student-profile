package com.portal.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for secure password hashing using SHA-256.
 * SHA-256 is built into the Java Standard Library and requires no external dependencies.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param plainPassword The plain text password
     * @return Hexadecimal string representation of the 256-bit hash (64 characters)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in standard Java runtime", e);
        }
    }

    /**
     * Verifies if a given plain-text password matches a stored SHA-256 hash.
     *
     * @param plainPassword  The plain text password entered by user
     * @param hashedPassword The existing hash stored in the database
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String calculatedHash = hashPassword(plainPassword);
        // Use MessageDigest.isEqual for constant-time comparison to prevent timing attacks
        return MessageDigest.isEqual(
                calculatedHash.getBytes(StandardCharsets.UTF_8),
                hashedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Converts a byte array to a lowercase hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
