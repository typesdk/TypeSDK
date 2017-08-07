/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MD5Util {

	 /** 
     * 默认的密码字符串组合
     */ 
	protected static char hexChar[] = {'0', '1', '2', '3', '4', '5', '6', '7',    
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	protected static MessageDigest messagedigest = null;  
    static {  
        try {  
            messagedigest = MessageDigest.getInstance("MD5");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
    }  
    public static String getFileMD5String(File file) throws IOException {  
        InputStream fis;  
        fis = new FileInputStream(file);  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
        while ((numRead = fis.read(buffer)) > 0) {  
            messagedigest.update(buffer, 0, numRead);  
        }  
        fis.close();  
        return bufferToHex(messagedigest.digest());  
    }  
  
    private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }  
  
    private static String bufferToHex(byte bytes[], int m, int n) {  
        StringBuffer stringbuffer = new StringBuffer(2 * n);  
        int k = m + n;  
        for (int l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return stringbuffer.toString();  
    }  
  
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
        char c0 = hexChar[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换  
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同  
        char c1 = hexChar[bt & 0xf];// 取字节中低 4 位的数字转换  
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    }  
	
	public static String md5(String string) {

	    byte[] hash;

	    try {

	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

	    } catch (NoSuchAlgorithmException e) {

	        throw new RuntimeException("Huh, MD5 should be supported?", e);

	    } catch (UnsupportedEncodingException e) {

	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);

	    for (byte b : hash) {

	        if ((b & 0xFF) < 0x10) hex.append("0");

	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    
	    return hex.toString();
	}
	
	public static String MD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String toUpperCase(byte[] data, int start, int len) {
		int end = start + len;
		int dist = 'A' - 'a';
		String srt = "";
		for (int i = start; i < end; i++) {
			if (data[i] >= 'a' && data[i] <= 'z') {
				data[i] += dist;
			}
		}
		try {
			srt = new String(data,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return srt;
	}
	public static String replaceBlank(String _in_str)
	{
		   Pattern p = Pattern.compile("//s*|/t|/r|/n");   
		      
		   Matcher m = p.matcher(_in_str);   
		   String after = m.replaceAll("");
		   return after;
	}
}
