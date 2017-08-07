package com.type.sdk.android.anzhi.util;

import com.anzhi.sdk.middle.util.Base64;




public abstract class SecurityUtil {
    private final static String BASE_TABLE = "0123456789ABCDEF";

    protected abstract byte[] encrypt(byte[] keybyte, byte[] src);

    protected abstract byte[] decrypt(byte[] keybyte, byte[] src);

    /**
     * 把16进制字符串转换成字节数组
     * 
     * @param hex
     * @return
     */
    public final byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private byte toByte(char c) {
        byte b = (byte) BASE_TABLE.indexOf(c);
        return b;
    }

    /**
     * 把字节数组转换成16进制字符串
     * 
     * @param bArray
     * @return
     */
    public final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public final String encryptToBase64(String key, String src, String encoding) {
        try {
            return Base64.encodeToString(encrypt(key.getBytes(), src.getBytes(encoding)));
        } catch (Exception ex) {
            return null;
        }
    }

    public final String decryptFromBase64(String key, String src, String encoding) {
        try {
            return new String(decrypt(key.getBytes(), Base64.decode(src)), encoding);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final String encryptToBase64UTF8(String key, String src) {
        return encryptToBase64(key, src, "UTF-8");
    }

    public final String decryptFromBase64UTF8(String key, String src) {
        return decryptFromBase64(key, src, "UTF-8");
    }

    public final String encryptToBase64DefaultEncoding(String key, String src) {
        try {
            return Base64.encodeToString(encrypt(key.getBytes(), src.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public final String decryptFromBase64DefaultEncoding(String key, String src) {
        try {
            return new String(decrypt(key.getBytes(), Base64.decode(src)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final String encryptToHex(String key, String src, String encoding) {
        try {
            return bytesToHexString(encrypt(key.getBytes(), src.getBytes(encoding)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final String decryptFromHex(String key, String src, String encoding) {
        try {
            return new String(decrypt(key.getBytes(), hexStringToByte(src)), encoding);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final String encryptToHexUTF8(String key, String src) {
        return encryptToHex(key, src, "UTF-8");
    }

    public final String decryptFromHexUTF8(String key, String src) {
        return decryptFromHex(key, src, "UTF-8");
    }

    public final String encryptToHexDefaultEncoding(String key, String src) {
        try {
            return bytesToHexString(encrypt(key.getBytes(), src.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final String decryptFromHexDefaultEncoding(String key, String src) {
        try {
            return new String(decrypt(key.getBytes(), hexStringToByte(src)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public final static String decodeBase64(String str) {
        if ("".equals(str) && null == str) {
            return null;
        } else {
            try {
                byte[] b = Base64.decode(str);
                return new String(b);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public final static String encodeBase64(String src) {
        if (!"".equals(src) && null != src) {
            return Base64.encodeToString(src.getBytes());
        } else {
            return Base64.encodeToString("N".getBytes());
        }
    }

}