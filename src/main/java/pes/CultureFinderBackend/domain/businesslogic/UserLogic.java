package pes.CultureFinderBackend.domain.businesslogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pes.CultureFinderBackend.controllers.dtos.AssistanceDTO;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.controllers.dtos.UserAuthenticationDTO;
import pes.CultureFinderBackend.domain.services.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserLogic {

    /*
     * Descripció: Instància de la classe que s'encarrega de gestionar els algorismes d'encripació
     */
    private static final ISecurityService iSecurityService = new SecurityService();

    /*
    * Descripció: Instància del servei de domini que interactua amb el servei de dades de les Assistències
    */
    @Autowired
    private IDomainAssistanceService iDomainAssistanceService;

    /*
     * Descripció: Instància del servei de domini que interactua amb el servei de dades dels esdeveniments
     */
    @Autowired
    private IDomainEventService iDomainEventService;

    /*
     * Descripció: Instància del servei de domini que envia notificacions al Firebase
     */
    @Autowired
    private INotificationService iNotificationService;

    /*
     * Descripció: Obté l'API token d'un usuari que es vol loguejar
     * <dto>: Conté l'identificador de l'usuari que es vol loguejar
     */
    public static String getApiToken(UserAuthenticationDTO dto) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappedDTO = mapper.convertValue(dto, Map.class);

        return iSecurityService.buildApiTokenFromOMappedbject(mappedDTO);
    }

    /*
    * Descripció: Comprova si algun usuari assisteix a un esdeveniment que té lloc el dia següent de la comprovació,
    * i si és així es fa una crida per a enviar-li una notificació
    */
    public void checkNearEvents() {
        List<AssistanceDTO> a = iDomainAssistanceService.getAllAssistances(0, 10, false).getContent();
        Set<String> userIds = new HashSet<>();
        EventDTO event;
        LocalDate today = LocalDate.now();

        for (AssistanceDTO assistance : a) {
            event = iDomainEventService.findById(assistance.getEventId()).get();
            if (event.getDataInici().equals(today.plusDays(1))) userIds.add(assistance.getUserId());
        }

        for (String userId : userIds) {
            iNotificationService.sendNearEventsNotification(userId);
        }
    }
}
