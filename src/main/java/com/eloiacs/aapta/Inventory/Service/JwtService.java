package com.eloiacs.aapta.Inventory.Service;

import com.eloiacs.aapta.Inventory.Models.AuthModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    public String generateJWToken(String userName, AuthModel model) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", model.getId());
        claims.put("email", model.getEmail());
        claims.put("role", model.getRoleId());
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder().setClaims(claims).setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(getSecurityKey(), SignatureAlgorithm.HS256).compact();
    }

    public String extractuserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractuserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }
        catch(ExpiredJwtException ejwt) {
            return null;
        }
    }

    public HashMap<String, Object> extractUserInformationFromToken(String token) {

        try {
            Claims claims = extractAllClaims(token.substring(7));

            return new HashMap<>(claims);
        }
        catch (ExpiredJwtException | NullPointerException expiredJwtException) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSecurityKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSecurityKey() {
        byte[] keyInBytes = "H5E6E65E34342VH3H45HGG5342YVY22YVW2YV3YY32YV32JV2YE2E".getBytes();
        return Keys.hmacShaKeyFor(keyInBytes);
    }
}
