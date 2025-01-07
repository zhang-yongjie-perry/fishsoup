package com.fishsoup.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

        public static String getMD5Hash(String input) {
            try {
                // 获取MD5算法的MessageDigest实例
                MessageDigest md = MessageDigest.getInstance("MD5");

                // 将输入字符串转换为字节数组，并更新MessageDigest
                md.update(input.getBytes());

                // 计算哈希值，得到字节数组
                byte[] digest = md.digest();

                // 将字节数组转换为十六进制字符串
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }

                // 返回十六进制字符串表示的哈希值
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5算法不可用", e);
            }
        }
}
