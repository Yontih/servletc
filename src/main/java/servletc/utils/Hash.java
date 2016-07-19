package servletc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Yonti on 16/07/2016.
 */
public class Hash {

    public static String md5(String value) {
        return Hash.process(value, "MD5");
    }

    public static String sha1(String value) {
        return Hash.process(value, "SHA-1");
    }

    public static String process(String value, String algorithm) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = value.getBytes("UTF-8");
            byte[] digest = md.digest(bytes);

            result = Hash.toString(digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String toString(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
