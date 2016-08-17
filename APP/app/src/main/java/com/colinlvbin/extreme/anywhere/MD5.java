package com.colinlvbin.extreme.anywhere;

import java.security.MessageDigest;

/**
 * Created by Colin on 2016/8/15.
 */
public class MD5 {
    public static String GetMD5(String val){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray=val.getBytes("UTF-8");
            byte[] md5Bytes=md5.digest(byteArray);
            StringBuffer buffer=new StringBuffer();
            for(int i=0;i<md5Bytes.length;i++){
             int value=((int)md5Bytes[i]&0xff);
                if(value<16){
                    buffer.append('0');
                }
                buffer.append(Integer.toHexString(value));
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
