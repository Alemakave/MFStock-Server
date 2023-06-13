package ru.alemakave.slib.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

public class StringUtils {
    public static String customQuote(String string) {
        string = string.replaceAll("%", "\u0000");
        string = string.replaceAll(" ", "%20");
        string = string.replaceAll("\\^", "%5E");
        string = string.replaceAll(",", "%2C");
        string = string.replaceAll("\\?", "%3F");
        // Replace ru chars to url code
        for (int i = 0; i < 48; i++) {
            char[] code = Integer.toHexString(53392+i).toUpperCase().toCharArray();
            String replacement = "%" + String.format("%s%s", code[0], code[1]) + "%" + String.format("%s%s", code[2], code[3]);
            string = string.replaceAll(String.valueOf(Character.toChars(1040 + i)[0]), replacement);
        }
        for (int i = 0; i < 16; i++) {
            char[] code = Integer.toHexString(53632+i).toUpperCase().toCharArray();
            String replacement = "%" + String.format("%s%s", code[0], code[1]) + "%" + String.format("%s%s", code[2], code[3]);
            string = string.replaceAll(String.valueOf(Character.toChars(1040 + i + 48)[0]), replacement);
        }
        string = string.replaceAll("\u0000", "%25");
        return string;
    }

    public static String customUnquote(String string) {
        string = string.replaceAll("%25", "\u0000");
        string = string.replaceAll("%20", " ");
        string = string.replaceAll("%5E", "^");
        string = string.replaceAll("%2C", ",");
        string = string.replaceAll("%D1%91", "ё");
        string = string.replaceAll("%28", "(");
        string = string.replaceAll("%29", ")");
        for (int i = 0; i < 48; i++) {
            char[] code = Integer.toHexString(53392+i).toUpperCase().toCharArray();
            String replacement = "%" + String.format("%s%s", code[0], code[1]) + "%" + String.format("%s%s", code[2], code[3]);
            string = string.replaceAll(replacement, String.valueOf(Character.toChars(1040 + i)[0]));
        }
        for (int i = 0; i < 16; i++) {
            char[] code = Integer.toHexString(53632+i).toUpperCase().toCharArray();
            String replacement = "%" + String.format("%s%s", code[0], code[1]) + "%" + String.format("%s%s", code[2], code[3]);
            string = string.replaceAll(replacement, String.valueOf(Character.toChars(1040 + i + 48)[0]));
        }
        if (string.contains("%")) {
            ArrayList<String> unknownChars = new ArrayList<>();
            String[] subStringsForFindUnknownChar = string.split("%");
            if (subStringsForFindUnknownChar.length > 1)
                for (int i = 1; i < subStringsForFindUnknownChar.length; i++) {
                    unknownChars.add("%" + subStringsForFindUnknownChar[i].substring(0, 2));
                }
            Logger.errorF("Error replacement, not found char %s (String: %s)\n", unknownChars.toString(), string);
        }
        string = string.replaceAll("\u0000", "%");
        return string;
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            String var10000;
            try {
                var10000 = quote(string, sw).toString();
            } catch (IOException var5) {
                return "";
            }

            return var10000;
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string != null && string.length() != 0) {
            char c = 0;
            int len = string.length();
            w.write(34);

            for(int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch(c) {
                    case '\b':
                        w.write("\\b");
                        continue;
                    case '\t':
                        w.write("\\t");
                        continue;
                    case '\n':
                        w.write("\\n");
                        continue;
                    case '\f':
                        w.write("\\f");
                        continue;
                    case '\r':
                        w.write("\\r");
                        continue;
                    case '"':
                    case '\\':
                        w.write(92);
                        w.write(c);
                        continue;
                    case '/':
                        if (b == '<') {
                            w.write(92);
                        }

                        w.write(c);
                        continue;
                }

                if (c >= ' ' && (c < 128 || c >= 160) && (c < 8192 || c >= 8448)) {
                    w.write(c);
                } else {
                    w.write("\\u");
                    String hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                }
            }

            w.write(34);
            return w;
        } else {
            w.write("\"\"");
            return w;
        }
    }

    public static String unquote(String a) {
        Properties prop = new Properties();
        try {
            prop.load(new ByteArrayInputStream(("x=" + a).getBytes()));
        }
        catch (IOException ignore) {}
        return prop.getProperty("x");
    }
}
