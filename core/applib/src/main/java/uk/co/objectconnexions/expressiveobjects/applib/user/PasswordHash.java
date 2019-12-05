package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import uk.co.objectconnexions.expressiveobjects.applib.value.Password;


public class PasswordHash {

    private static final String DELIMITER = " ";
    private static final int HASH_ITERATIONS = 967;
    private static final SaltGenerator saltGenerator = new RandomSaltGenerator();
    
    private PasswordHash() {}

    public static Password createPassword(String password) {
        return new Password("\u00f7" + hash1(password));
    }

    /*
    private static String hash0(String password) {
        return type("0") + rot13(password);
    }
     */
    
    private static String hash1(String password) {
        byte[] salt = saltGenerator.generateSalt();
        return type("1") + byteToBase64(salt) + DELIMITER + digest(password, salt);
    }

    private static String type(String type) {
        return type + DELIMITER;
    }

    public static boolean check(Password storedPassword, Password passwordAttempt) {
        String referencePassword = storedPassword.getPassword();
        String hashedPassword = passwordAttempt.getPassword();
        if (referencePassword.startsWith("\u00f7")) {
            String[] split = referencePassword.substring(1).split(DELIMITER);
            String type = split[0];
            if (type.equals("0")) {
                referencePassword = split[1];
                hashedPassword = rot13(hashedPassword);
            } else if (type.equals("1")) {
                referencePassword = split[2];
                byte[] salt = base64ToByte(split[1]);
                hashedPassword = digest(hashedPassword, salt);
            }
        }

        return referencePassword.equals(hashedPassword);
    }

    private static String rot13(String s) {
        StringBuffer encrypted = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            encrypted.append(c);
        }
        return encrypted.toString();
    }
    
    private static String digest(String password, byte[] salt) {
        byte[] raw = getSHA1Hash(HASH_ITERATIONS, password, salt);
        return byteToBase64(raw);
    }

    private static byte[] getSHA1Hash(int hashIterations, String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(salt);
            byte[] input = digest.digest(password.getBytes("UTF-8"));
            for (int i = 0; i < hashIterations; i++) {
                digest.reset();
                input = digest.digest(input);
            }
            return input;
        } catch (UnsupportedEncodingException e) {
            throw new PasswordException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordException(e);
        }
    }

    private static byte[] base64ToByte(String data) {
        return Base64.getDecoder().decode(data);
    }

    private static String byteToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);        
    }

}
