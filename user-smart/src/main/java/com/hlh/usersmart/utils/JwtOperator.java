package com.hlh.usersmart.utils;

import com.google.common.collect.Maps;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: JWT工具类
 * @author: mqxu
 * @date: 2022-10-04
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtOperator {
    /**
     * 秘钥
     */
    @Value("${jwt.secret:aaaaaaaaaabbbbbbbbbbbcccccccccccddddddd}")
    private String secret;
    /**
     * 有效期，单位秒
     * 默认2周
     */
    @Value("${jwt.expire-time-in-second:1209600}")
    private Long expirationTimeInSecond;

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim
     */
    public Claims getClaimsFromToken(String token) {
        try {
            //return Jwts.builder().setClaims()
            return Jwts.parser().setSigningKey(this.secret.getBytes()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("token解析错误", e);
            throw new IllegalArgumentException("Token invalided.");
        }
    }

    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    public Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + this.expirationTimeInSecond * 1000);
    }

    /**
     * 为指定用户生成token
     *
     * @param claims 用户信息
     * @return token
     */
    public String generateToken(Map<String, Object> claims) {
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();

        byte[] keyBytes = secret.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder().setClaims(claims).setIssuedAt(createdTime).setExpiration(expirationTime)
                // 也可以改用你喜欢的算法
                // 支持的算法详见：https://github.com/jwtk/jjwt#features
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public static void main(String[] args) {
        // 1. 初始化
        JwtOperator jwtOperator = new JwtOperator();
        jwtOperator.expirationTimeInSecond = 1209600L;
        jwtOperator.secret = "aaaaaaaaaabbbbbbbbbbbcccccccccccddddddd";
        //jwtOperator.secret = "abc123";

        // 2.设置用户信息
        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("id", "1");
        objectObjectHashMap.put("wxNickname", "wd");
        objectObjectHashMap.put("role", "user");

        // 测试1: 生成token
        String token = jwtOperator.generateToken(objectObjectHashMap);
        //会生成类似该字符串的内容: eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJpYXQiOjE1NjU1ODk4MTcsImV4cCI6MTU2Njc5OTQxN30.27_QgdtTg4SUgxidW6ALHFsZPgMtjCQ4ZYTRmZroKCQ
        System.out.println(token);

        // 将这串字符还原为上面生成的token!!!
        // String someToken = "eyJhbGciOiJIUzI1NiJ9.eyJuaWNrbmFtZSI6Im1xeHUiLCJpZCI6IjEiLCJpYXQiOjE2NjQ4NjU4MDUsImV4cCI6MTY2NjA3NTQwNX0.eaGZkPhC_4QJXkIcq-1yqPrcOvtZxxUFszE5tdOBY-A";
         //测试2: 如果能token合法且未过期，返回true
        //Boolean validateToken = jwtOperator.validateToken(someToken);
        //System.out.println(validateToken);

        //// 测试3: 获取用户信息
        // Claims = jwtOperator.getClaimsFromToken(someToken);
        //System.out.println(claims);
        //
        //// token的第一段（以.为边界）
        //String encodedHeader = "eyJhbGciOiJIUzI1NiJ9";
        // 测试4: 解密Header
        //byte[] header = Base64.decodeBase64(encodedHeader.getBytes());
        //System.out.println(new String(header));
        //
        // token的第二段（以.为边界）
        //String encodedPayload = "eyJuaWNrbmFtZSI6Im1xeHUiLCJpZCI6IjEiLCJpYXQiOjE2NjQ4NjU4MDUsImV4cCI6MTY2NjA3NTQwNX0";
        ////// 测试5: 解密Payload
        //byte[] payload = Base64.decodeBase64(encodedPayload.getBytes());
        //System.out.println(new String(payload));
        // 测试6: 这是一个被篡改的token，因此会报异常，说明JWT是安全的
        boolean flag = jwtOperator.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJuaWNrbmFtZSI6Im1xeHUiLCJpZCI6IjEiLCJpYXQiOjE2NjQ4NjU4MDUsImV4cCI6MTY2NjA3NTQwNX0.eaGZkPhC_4QJXkIcq-1yqPrcOvtZxxUFszE5tdOBY-A");
        System.out.println(flag);
        }
    }
