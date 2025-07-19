package feelingalive.advancedcaptcha.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class TokenGenerator {
    public static String generateToken(UUID playerId, long time, String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = playerId.toString() + time + ip + (long) (Math.random() * Long.MAX_VALUE);
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString();
        }
    }
}