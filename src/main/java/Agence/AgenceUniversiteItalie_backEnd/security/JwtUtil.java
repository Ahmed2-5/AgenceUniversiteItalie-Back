package Agence.AgenceUniversiteItalie_backEnd.security;


import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Utilisateur user) {
        Map<String, Object> claims = new HashMap<>();
        String roleName = (user.getRole() != null) ? user.getRole().getLibelleRole().name() : "SUPER_ADMIN";
        claims.put("role", roleName);
        return createToken(claims, user.getAdresseMail());
    }



    public String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY)
                .compact();
    }

   public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
   }

   public <T> T extractClaim(String token , Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
   }

   private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
   }

   public Boolean validateToken(String token , Utilisateur utilisateur){
        final String username = extractUsername(token);
        return (username.equals(utilisateur.getAdresseMail()) && !isTokenExpired(token));
   }

   private Boolean isTokenExpired(String token){
        return extractClaim(token , Claims::getExpiration).before(new Date());
   }
}
