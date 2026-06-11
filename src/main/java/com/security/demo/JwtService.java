package com.security.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService 
{
	
	private final SecretKey key = 
			Keys.hmacShaKeyFor(
					"abcdefghijklmnopqrstuvwxyz12345678901234567890".getBytes()
					);
	
	public String generateToken(User user)
	{
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole());
		return createToken(claims, user);
	}

	private String createToken(Map<String, Object> claims, User user) 
	{
		 return Jwts.builder()
				 .subject(user.getUsername())
				 .claims(claims)
				 .issuedAt(new Date(System.currentTimeMillis()))
				 .expiration(new Date(System.currentTimeMillis() + 1000*60*60))
				 .signWith(key)
				 .compact();
	}
	
	public String extractUsername(String token) {
	    return Jwts.parser()
	            .verifyWith(key)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .getSubject();
	}
	
	public String extractRole(String token)
	{
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
	}

	public boolean validateToken(String token, UserDetails userDetails) {

	    String username = extractUsername(token);

	    return username.equals(userDetails.getUsername())
	            && !isTokenExpired(token);
	}

	public boolean isTokenExpired(String token) {

	    return Jwts.parser()
	            .verifyWith(key)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .getExpiration()
	            .before(new java.util.Date());
	}
}
