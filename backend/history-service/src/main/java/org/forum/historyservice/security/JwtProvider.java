package org.forum.historyservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;

    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request) {
        String prefixedToken = request.getHeader("Authorization");
        if(prefixedToken == null || prefixedToken.length()<8) return Optional.of(null);
        String token = prefixedToken.substring(7);
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        List<LinkedHashMap<String, String>> permissions =
                (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());

        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .username(username)
                .authorities(authorities)
                .build());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public int extractUserID(String token) {
        return extractAllClaims(token).get("userID", Integer.class);
    }
}
