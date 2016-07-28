package servletc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yonti on 22/07/2016.
 */
public class Convert {

    public static float toFloat(String s, float def) {
        float n = -1;
        try {
            n = Float.parseFloat(s);
        } catch (Exception e) {
            n = def;
        }

        return n;
    }

    public static double toDouble(String s, double def) {
        double n = -1;
        try {
            n = Double.parseDouble(s);
        } catch (Exception e) {
            n = def;
        }

        return n;
    }

    public static int toInt(String s, int def) {
        int n = def;
        try {
            n = Integer.parseInt(s);
        } catch (Exception e) {
            n = def;
        }

        return n;
    }

    public static short toShort(String s, short def) {
        short n = def;
        try {
            n = Short.parseShort(s);
        } catch (Exception e) {
            n = def;
        }

        return n;
    }

    public static long toLong(String s, int def) {
        long n = def;
        try {
            n = Long.parseLong(s);
        } catch (Exception e) {
            n = def;
        }

        return n;
    }

    public static String streamToStringSafe(InputStream is) {
        try {
            return Convert.streamToString(is);
        } catch (Exception e) {
        }

        return null;
    }

    public static String streamToString(InputStream is) throws Exception {
        Exception exception = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is, "UTF8"));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    exception = e;
                }
            }

            if (exception != null) {
                throw exception;
            }
        }

        return sb.toString();
    }

}
