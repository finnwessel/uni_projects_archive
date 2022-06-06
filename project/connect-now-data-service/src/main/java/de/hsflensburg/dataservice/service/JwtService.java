package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.configuration.RsaConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JwtService {

    private RsaConfig rsaConfig;

    @Autowired
    public void setRsaConfig(RsaConfig rsaConfig) {
        this.rsaConfig = rsaConfig;
    }

    public boolean isValidAccessToken(String token) {
        Optional<Claims> claims = getClaims(token);

        if (claims.isEmpty()) {
            return false;
        }

        Object tokenType = claims.get().get("type");

        return tokenType != null && tokenType.equals("access");
    }

    public Optional<Claims> getClaims(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(rsaConfig.getPublicKey()).parseClaimsJws(token).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(rsaConfig.getPublicKey()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public List<? extends GrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(rsaConfig.getPublicKey()).parseClaimsJws(token).getBody();

        ArrayList<String> authorities = (ArrayList<String>) claims.get("authorities");
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }
}
