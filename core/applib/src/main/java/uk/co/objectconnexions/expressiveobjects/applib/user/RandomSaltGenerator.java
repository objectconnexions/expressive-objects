package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class RandomSaltGenerator implements SaltGenerator {

    public byte[] generateSalt() {
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordException(e);
        }
    }

}
