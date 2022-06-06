package de.hsflensburg.authservice.service;

import de.hsflensburg.authservice.configuration.RsaConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.accessTokenExpirationInMin}")
    private int accessTokenExpirationInMin;

    @Value("${jwt.refreshTokenExpirationInMin}")
    private int refreshTokenExpirationInMin;

    private RsaConfig rsaConfig;

    private TokenService tokenService;

    @Autowired
    public void setRsaConfig(RsaConfig rsaConfig) {
        this.rsaConfig = rsaConfig;
    }

    @Autowired
    public void setUserService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String generateAccessToken(Collection<? extends GrantedAuthority> roles, String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(addMinutesToDate(accessTokenExpirationInMin, new Date()))
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("type", "access")
                .claim("authorities", AuthorityUtils.authorityListToSet(roles))
                .signWith(SignatureAlgorithm.RS256, rsaConfig.getPrivateKey())
                .compact();
    }

    public String generateRefreshToken(String subject) {
        UUID tokenId = UUID.randomUUID();

        Date expires = addMinutesToDate(refreshTokenExpirationInMin, new Date());
        tokenService.insertToken(subject, tokenId.toString(), expires);

        return Jwts.builder()
                .claim("type", "refresh")
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expires)
                .setId(tokenId.toString())
                .signWith(SignatureAlgorithm.RS256, rsaConfig.getPrivateKey())
                .compact();
    }

    public boolean isValidAccessToken(String token) {
        Optional<Claims> claims = getClaims(token);

        if (claims.isEmpty()) {
            return false;
        }

        Object tokenType = claims.get().get("type");

        return tokenType != null && tokenType.equals("access");

    }

    public boolean isValidRefreshToken(String token) {
        Optional<Claims> claims = getClaims(token);

        if (claims.isEmpty()) {
            return false;
        }

        Object tokenType = claims.get().get("type");

        if (tokenType == null || !tokenType.equals("refresh")) {
            return false;
        }

        return tokenService.existsToken(claims.get().getId());
    }

    public Optional<Claims> getClaims(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(rsaConfig.getPublicKey()).parseClaimsJws(token).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getUsernameFromToken(String token) {
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

    private static Date addMinutesToDate(int expiredAfterMs, Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;
        long curTimeInMs = beforeTime.getTime();
        return new Date(curTimeInMs + (expiredAfterMs * ONE_MINUTE_IN_MILLIS));
    }
}
