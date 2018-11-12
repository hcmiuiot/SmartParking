package main;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherUtils {

    private static CipherUtils instance = null;

    private static Cipher ecipher;
    private static Cipher dcipher;

    private static SecretKey key;

    public static CipherUtils getInstance() {
        if (instance == null) {
            instance = new CipherUtils();
            try {
                key = getRandomKey();
                ecipher = Cipher.getInstance("DES");
                dcipher = Cipher.getInstance("DES");
                ecipher.init(Cipher.ENCRYPT_MODE, key);
                dcipher.init(Cipher.DECRYPT_MODE, key);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static SecretKey getRandomKey() {
        try {
            SecretKey _key = KeyGenerator.getInstance("DES").generateKey();
            System.out.println(new String(_key.getEncoded()));
            return _key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            enc = BASE64EncoderStream.encode(enc);
            return new String(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String str) {
        byte[] dec = BASE64DecoderStream.decode(str.getBytes());
        byte[] utf8;
        try {
            utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}


