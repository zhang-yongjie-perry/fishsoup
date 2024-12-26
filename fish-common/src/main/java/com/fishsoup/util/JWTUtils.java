package com.fishsoup.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

public class JWTUtils {

    /** 过期时间 */
    private static final Long JWT_DDL = 1000 * 60 * 30L;

    /** 设置盐 */
    private static final String JWT_KEY = "fish1q2w3e";

    /** 签发者 */
    public static final String ISSUER_FISH = "fish_soup";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        return Jwts.builder()
            .setId(getUUID())
            .setSubject(subject) // 载荷
            .setIssuer(ISSUER_FISH)
            .setIssuedAt(DateUtils.now())
            .signWith(SignatureAlgorithm.HS256, generateSecretKey())
            .compact();
    }

    public static SecretKey generateSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(JWT_KEY);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
            .setSigningKey(generateSecretKey    ())
            .parseClaimsJws(jwt)
            .getBody();
    }
}
