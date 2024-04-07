package zerobase.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    private static final String KEY_ROLE = "role";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
    private final SecretKey secretKey;
    public TokenProvider (@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

    }

    public String generateToken(String mail, String role) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() +TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .claim(KEY_ROLE, role)
                .claim(Claims.SUBJECT, mail)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
    public String getRole(String token) {
        return (String) this.parseClaims(token).get(KEY_ROLE);
    }
    public String getMail(String token) {
        return this.parseClaims(token).getSubject();
    }
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).build().parseSignedClaims(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
