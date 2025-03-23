package com.irmaktekin.task.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        Collection< ? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        claims.put("roles",authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        //claims.put("iss","https://secure.genuinecoder.com");
        SecretKey key = generateKey();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact();
    }
    private SecretKey generateKey(){
        byte [] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private Claims getClaims(String jwt){
        SecretKey key = generateKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
    public boolean isTokenValid(String jwt){
        try{
            Claims claims = getClaims(jwt);
            return claims.getExpiration().after(Date.from(Instant.now()));
        }
        catch (Exception e){
            Logger.getLogger(JwtService.class.getName()).severe("Invalid JWT: " + e.getMessage());
            return false;
        }

    }
    public String extractUsername(String jwt){
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }
}

