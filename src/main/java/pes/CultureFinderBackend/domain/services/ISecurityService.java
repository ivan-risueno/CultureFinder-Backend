package pes.CultureFinderBackend.domain.services;

import java.util.Map;

public interface ISecurityService {
    String buildApiTokenFromOMappedbject(Map<String, Object> mappedObjectToMap);
    Map<String, Object> buildMappedObjectFromApiToken(String apiToken);
}
