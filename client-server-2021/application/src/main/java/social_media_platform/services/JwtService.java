package social_media_platform.services;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

import java.util.Date;

import io.jsonwebtoken.*;

public class JwtService {
    public static String SECRET_KEY = "3_TIMES_SUPER_SECRET_STRING_SUPER_SECRET_STRING_SUPER_SECRET_STRING";

    /**
     * Creates a new JWT token
     *
     * @param id
     * @param issuer the issuer
     * @param subject the subject
     * @param minutes
     * @return a JWT token
     */
    public String createJWT(String id, String issuer, String subject, int minutes) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        // If it has been specified, let's add the expiration
        if (minutes > 0) {
            long expMillis = nowMillis + (minutes * 60000L);
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Decodes a JWT token
     *
     * @param jwt a JWT token
     * @return the decoded token
     * @throws Exception
     */
    public static Claims decodeJWT(String jwt) throws Exception {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY)).build();
        // This line will throw an exception if it is not a signed JWS (as expected)
        return jwtParser.parseClaimsJws(jwt).getBody();
    }
}
