package pes.CultureFinderBackend.domain.services;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.domain.businesslogic.JWTLogic;

import java.util.Map;

@Service
public class SecurityService implements ISecurityService {

    /*
     * Descripció: Obté una API Token donat un objecte mappejat
     */
    public String buildApiTokenFromOMappedbject(Map<String, Object> mappedObjectToMap) {
        return JWTLogic.dynamicBuilderSpecific(mappedObjectToMap, SignatureAlgorithm.HS256);
    }

    /*
     * Descripció: Obté un objecte mappejat a partir d'una API Token
     */
    public Map<String, Object> buildMappedObjectFromApiToken(String apiToken) {
        return JWTLogic.parseJWT(apiToken).getBody();
    }
}
