/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final FcmPropsConfig fcmPropsConfig;
    private final String secretKey;

    public JwtTokenUtil(FcmPropsConfig fcmPropsConfig) {
        this.fcmPropsConfig = fcmPropsConfig;
        this.secretKey = Base64Codec.BASE64.encode(fcmPropsConfig.getSecretKey());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        // decrypt token
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateAccessToken(String username, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        return doGenerateToken(claims, username, fcmPropsConfig.getJwt().getExpirationTime());
    }

    public String generateRefreshToken(String username, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        return doGenerateToken(claims, username, fcmPropsConfig.getJwt().getRefreshExpirationTime());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, Long expiration) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expiration * 1000);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(subject)
                   .setIssuedAt(createdDate)
                   .setExpiration(expirationDate)
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    public Boolean canRefresh(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        return !isTokenExpired(refreshToken);
    }

    public String refreshToken(String refreshToken) {
        final Claims claims = getAllClaimsFromToken(refreshToken);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + fcmPropsConfig.getJwt().getRefreshExpirationTime() * 1000));
        return Jwts.builder()
                   .setClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
