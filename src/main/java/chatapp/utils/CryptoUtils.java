package chatapp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String SALT = "HCMUS_CHAT_SECRET_SALT";
    private static final String PREFIX = "ENC:";

    private static SecretKeySpec getKey(UUID conversationId) {
        try {
            String seed = conversationId.toString() + SALT;
            byte[] key = seed.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String strToEncrypt, UUID conversationId) {
        if (strToEncrypt == null)
            return null;
        if (conversationId == null)
            return strToEncrypt; 

        try {
            SecretKeySpec secretKey = getKey(conversationId);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return PREFIX
                    + Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.println("Error while encrypting: " + e.toString());
        }
        return strToEncrypt;
    }

    public static String decrypt(String strToDecrypt, UUID conversationId) {
        if (strToDecrypt == null)
            return null;
        if (conversationId == null)
            return strToDecrypt;
        if (!strToDecrypt.startsWith(PREFIX))
            return strToDecrypt; 

        try {
            String cipherText = strToDecrypt.substring(PREFIX.length());
            SecretKeySpec secretKey = getKey(conversationId);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (Exception e) {
            System.err.println("Error while decrypting: " + e.toString());
        }
        return strToDecrypt;
    }
}
