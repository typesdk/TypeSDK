/*
Copyright (C) Huawei Technologies Co., Ltd. 2015. All rights reserved.
See LICENSE.txt for this sample's licensing information.
*/

package com.type.sdk.android.huawei;

/**
 * 将字符串用64位加密算法加密
 */
public class Base64Util
{
    
    static public String encode(byte[] data)
    {
        return encode(data, data.length);
    }
    
    static public String encode(byte[] data, int length)
    {
        char[] out = new char[((length + 2) / 3) * 4];
     
        for (int i = 0, index = 0; i < length; i += 3, index += 4)
        {
            boolean quad = false;
            boolean trip = false;
            
            int val = (0xFF & (int)data[i]);
            val <<= 8;
            if ((i + 1) < length)
            {
                val |= (0xFF & (int)data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < length)
            {
                val |= (0xFF & (int)data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        
        return new String(out);
    }
    
    public static byte[] decode(String data)
    {
       
        int tempLen = data.length();
        for (int ix = 0; ix < data.length(); ix++)
        {
            if ((data.charAt(ix) > 255) || codes[data.charAt(ix)] < 0)
                --tempLen;
          
        }
        
        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3)
            len += 2;
        if ((tempLen % 4) == 2)
            len += 1;
        
        byte[] out = new byte[len];
        
        int shift = 0; 
        int accum = 0; 
        int index = 0;
        
      
        for (int ix = 0; ix < data.length(); ix++)
        {
            int value = (data.charAt(ix) > 255) ? -1 : codes[data.charAt(ix)];
            
            if (value >= 0)
            {
                accum <<= 6;
                shift += 6; 
                accum |= value; 
                if (shift >= 8) 
                {
                    shift -= 8; 
                    out[index++] = 
                        (byte)((accum >> shift) & 0xff);
                }
            }
          
        }

        if (index != out.length)
        {
            return new byte[0];
        }
        
        return out;
    }

    static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
 
    static private byte[] codes = new byte[256];
    static
    {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte)(i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte)(26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte)(52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }
    
}