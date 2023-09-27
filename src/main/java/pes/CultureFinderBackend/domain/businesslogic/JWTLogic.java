package pes.CultureFinderBackend.domain.businesslogic;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JWTLogic {

    /*
     * Descripció: Instància de la classe que s'encarrega de gestionar els algorismes d'encriptació
     */
    private static final SecurityAlgorithmsLogic securityAlgorithmsLogic = new SecurityAlgorithmsLogic();

    /*
     * Descripció: Construeix una API token
     * <claims>: Objecte mappejat
     * <sig>: Tipus de algorisme de signatura a utilitzar
     */
    public static String dynamicBuilderSpecific(Map<String, Object> claims, SignatureAlgorithm sig) {
        JwtBuilder builder = Jwts.builder();

        claims.forEach((key, value) -> {
            switch (key) {
                case "iss" -> {
                    ensureType(key, value, String.class);
                    builder.setIssuer((String) value);
                }
                case "sub" -> {
                    ensureType(key, value, String.class);
                    builder.setSubject((String) value);
                }
                case "aud" -> {
                    ensureType(key, value, String.class);
                    builder.setAudience((String) value);
                }
                case "exp" -> {
                    ensureType(key, value, Long.class);
                    builder.setExpiration(Date.from(
                            Instant.ofEpochSecond(Long.parseLong(value.toString()))
                    ));
                }
                case "nbf" -> {
                    ensureType(key, value, Long.class);
                    builder.setNotBefore(Date.from(
                            Instant.ofEpochSecond(Long.parseLong(value.toString()))
                    ));
                }
                case "iat" -> {
                    ensureType(key, value, Long.class);
                    builder.setIssuedAt(Date.from(
                            Instant.ofEpochSecond(Long.parseLong(value.toString()))
                    ));
                }
                case "jti" -> {
                    ensureType(key, value, String.class);
                    builder.setId((String) value);
                }
                default -> builder.claim(key, value);
            }
        });

        switch(sig) {
            case HS256 -> builder.signWith(SignatureAlgorithm.HS256, securityAlgorithmsLogic.getHS256SecretBytes());
            case HS384 -> builder.signWith(SignatureAlgorithm.HS384, securityAlgorithmsLogic.getHS384SecretBytes());
            case HS512 -> builder.signWith(SignatureAlgorithm.HS512, securityAlgorithmsLogic.getHS512SecretBytes());
        }

        return builder.compact();
    }

    /*
     * Descripció: Comprova si el valor que es passa a un claim del Json Web Token té un valor correcte
     * <registeredClaim>: Nom del camp a comprovar
     * <value>: Valor del camp a comprovar
     * <expectedType>: Classe esperada que ha de tenir el valor a comprovar
     * <JwtException>: Exepció que es llença si la classe esperada no coincideix amb la classe de <value>
     */
    private static void ensureType(String registeredClaim, Object value, Class expectedType) {
        boolean isCorrectType =
                expectedType.isInstance(value) ||
                        expectedType == Long.class && value instanceof Integer;

        if (!isCorrectType) {
            String msg = "Expected type: " + expectedType.getCanonicalName() +
                    " for registered claim: '" + registeredClaim + "', but got value: " +
                    value + " of type: " + value.getClass().getCanonicalName();
            throw new JwtException(msg);
        }
    }

    /*
     * Descripció: Obté un objecte mappejat a partir d'un Json Web Token
     */
    public static Jws<Claims> parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKeyResolver(securityAlgorithmsLogic.getSigningKeyResolver())
                .parseClaimsJws(jwt);
    }
}
