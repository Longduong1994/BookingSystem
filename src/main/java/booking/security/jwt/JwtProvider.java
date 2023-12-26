package booking.security.jwt;

import booking.security.user_principle.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class JwtProvider {
    private Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${expired}")
    private Long EXPIRED;
    @Value("${secret}")
    private String SECRET;

    public String generateToken(UserPrincipal userPrincipal) {
        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRED))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return token;
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            logger.error("Expired Token {}", expiredJwtException.getMessage());
        } catch (MalformedJwtException malformedJwtException) {
            logger.error("Invalid format {}", malformedJwtException.getMessage());
        } catch (UnsupportedJwtException unsupportedJwtException) {
            logger.error("Unsupported token {}", unsupportedJwtException.getMessage());
        } catch (SignatureException signatureException) {
            logger.error("Invalid Signature token {}", signatureException.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

}
