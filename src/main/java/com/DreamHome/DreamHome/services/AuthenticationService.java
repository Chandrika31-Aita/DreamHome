package com.DreamHome.DreamHome.services;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {
    public String encryptPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public  boolean verifyPassword(String inputPassword, String encryptedPassword){
        BCryptPasswordEncoder validator = new BCryptPasswordEncoder();
        return validator.matches(inputPassword,encryptedPassword);
    }

    public String createJWT(String email, String sellerOrBuyerOrAdminId, long ttlMillis) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("This is a secret key This is a secret key This is a secret key This is a secret key This is a secret key");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("sellerOrBuyerOrAdminId", sellerOrBuyerOrAdminId);


        JwtBuilder builder = Jwts.builder()
                .setId(email)
                .setIssuedAt(now)
                .setClaims(claims)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public Map<String, Object> decodeJWT(String jwt) {
        try {
            // Clean the JWT token - remove "Bearer " prefix if present and trim whitespace
            if (jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7).trim();
            }
            jwt = jwt.trim();

            Map<String, Object> claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("This is a secret key This is a secret key This is a secret key This is a secret key This is a secret key"))
                    .parseClaimsJws(jwt).getBody();
            return claims;
        } catch (Exception e) {
            System.err.println("JWT Decode Error: " + e.getMessage());
            System.err.println("JWT Token: " + jwt);
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }




}
