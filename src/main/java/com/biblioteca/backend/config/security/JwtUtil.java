package com.biblioteca.backend.config.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// ESTA CLASE SIRVE PARA GENERAR Y MANEJAR EL TOKEN QUE CADA PETICION VA A SOLICITAR
@Service
public class JwtUtil {
    // ESTA ES LA CLAVE A TRAVES DE LA CUAL SE VA A GENERAR EL TOKEN
    // LLAVE OBTENIDA DESDE http://www.allkeysgenerator.com/
    // VALORES ALMACENADOS EN EL ARCHIVO PROPERTIES
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    // TIEMPO DE EXPIRACIÓN DEL TOKEN (EN MILISEGUNDOS)
    private static final int TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // MÈTODO PARA OBTENER EL NOMBRE DEL USUARIO A PARTIR DEL TOKEN RECIBIDO
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // MÈTODO PARA OBTENER LA FECHA DE EXPIRACION DEL TOKEN RECIBIDO
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // MÈTODO PARA OBTENER UN PRIVILEGIO DEL TOKEN RECIBIDO
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // MÈTODO PARA OBTENER TODOS LOS PRIVILEGIOS DEL TOKEN RECIBIDO
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // MÈTODO PARA SABER SI EL TOKEN HA EXPIRADO
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // MÈTODO PARA GENERAR EL TOKEN A PARTIR DE UN USUARIO AUTENTICADO Y REGISTRADO
    // POR EL OBJETO DE SPRING DE TIPO USERDETAILS
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // MÈTODO PARA CREAR UN TOKEN DESIGNANDO LOS PRIVILEGIOS DEL USUARIO (CLAIMS),
    // EL USUARIO, SU FECHA DE EXPIRACION, Y SU FIRMA (TIPO DE ENCRIPTACION DEL
    // TOKEN)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    // MÈTODO PARA VALIDAR UN TOKEN RECIBIDO SEGUN SU FECHA DE EXPIRACION Y EL
    // USUARIO PRESENTE EN ÉL
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}