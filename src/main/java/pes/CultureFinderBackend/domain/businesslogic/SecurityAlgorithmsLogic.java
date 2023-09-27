package pes.CultureFinderBackend.domain.businesslogic;

import io.jsonwebtoken.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

class SecurityAlgorithmsLogic { // Accés "package-scope"

    /*
     * Descripció: Clau secreta per a l'algorisme HS256
     */
    private final String HS256 = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    /*
     * Descripció: Clau secreta per a l'algorisme HS384
     */
    private final String HS384 = "VW96zL+tYlrJLNCQ0j6QPTp+d1q75n/Wa8LVvpWyG8pPZOP6AA5X7XOIlI90sDwx";

    /*
     * Descripció: Clau secreta per a l'algorisme HS512
     */
    private final String HS512 = "cd+Pr1js+w2qfT2BoCD+tPcYp9LbjpmhSMEJqUob1mcxZ7+Wmik4AYdjX+DlDjmE4yporzQ9tm7v3z/j+QbdYg==";

    /*
     * Descripció: Map que conté tots els algorismes d'encriptació i les seves claus
     */
    private Map<String, String> secrets;

    /*
     * Descripció: Construeix la instància del mapa amb els algorismes d'encriptació i les seves claus
     */
    public SecurityAlgorithmsLogic() {
        this.secrets = new HashMap<>();
        this.secrets.put(SignatureAlgorithm.HS256.getValue(), HS256);
        this.secrets.put(SignatureAlgorithm.HS384.getValue(), HS384);
        this.secrets.put(SignatureAlgorithm.HS512.getValue(), HS512);
    }

    /*
     * Descripció: Obté l'algorisme utilitzat per a encriptar el Json Web Token
     */
    private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
        @Override
        public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
            return Base64.getDecoder().decode(secrets.get(header.getAlgorithm()));
        }
    };

    /*
     * Descripció: Obté l'algorisme utilitzat per a encriptar el Json Web Token
     */
    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    /*
     * Descripció: Decodifica la key secreta de l'algorisme HS256
     */
    public byte[] getHS256SecretBytes() {
        return Base64.getDecoder().decode(secrets.get(SignatureAlgorithm.HS256.getValue()));
    }

    /*
     * Descripció: Decodifica la key secreta de l'algorisme HS384
     */
    public byte[] getHS384SecretBytes() {
        return Base64.getDecoder().decode(secrets.get(SignatureAlgorithm.HS384.getValue()));
    }

    /*
     * Descripció: Decodifica la key secreta de l'algorisme HS512
     */
    public byte[] getHS512SecretBytes() {
        return Base64.getDecoder().decode(secrets.get(SignatureAlgorithm.HS512.getValue()));
    }
}
