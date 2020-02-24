package dev.onyxstudios.truckersportal.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtils {

    //Password
    private static final String ID = "$31$";
    private static final int COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int SIZE = 128;
    private static Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");
    private static SecureRandom random = new SecureRandom();

    //Token
    private static final int TOKEN_LENGTH = 64;
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String hash(char[] password) {
        byte[] salt = new byte[SIZE / 8];
        random.nextBytes(salt);
        byte[] dk = pbkdf2(password, salt, 1 << COST);
        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();

        return ID + COST + '$' + enc.encodeToString(hash);
    }

    public static String generateToken() {
        Random random = new Random();
        char[] symbols = SYMBOLS.toCharArray();
        char[] buf = new char[TOKEN_LENGTH];

        for (int i = 0; i < buf.length; i++) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    public static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            return f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }

    public static boolean authenticate(char[] password, String token) {
        if(password != null && token != null) {
            Matcher matcher = layout.matcher(token);
            if (!matcher.matches())
                throw new IllegalArgumentException("Invalid token format");
            int iterations = iterations(Integer.parseInt(matcher.group(1)));
            byte[] hash = Base64.getUrlDecoder().decode(matcher.group(2));
            byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
            byte[] check = pbkdf2(password, salt, iterations);
            int zero = 0;
            for (int idx = 0; idx < check.length; ++idx)
                zero |= hash[salt.length + idx] ^ check[idx];

            return zero == 0;
        }

        return false;
    }

    public static int iterations(int cost) {
        if ((cost < 0) || (cost > 30))
            throw new IllegalArgumentException("cost: " + cost);

        return 1 << cost;
    }
}
