package pes.CultureFinderBackend.domain.businesslogic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import pes.CultureFinderBackend.domain.models.EventEntity;
import pes.CultureFinderBackend.domain.models.UnparsedEventEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static pes.CultureFinderBackend.controllers.EventController.getEventsAPIAgendaCultural;

public class EventLogic {

    /*
     * Descripció: Obté els esdeveniments de l'API de l'agenda cultural i els divideix en una llista
     * Resultat: Llista amb els esdeveniments de l'API de l'agenda cultural
     */
    public static List<EventEntity> fetchEvents() throws JsonProcessingException {
        ResponseEntity<String> events = getEventsAPIAgendaCultural();
        String json = events.getBody();

        return parseEvents(json, false);
    }

    /*
     * Descripció: Parseja un json que conté esdeveniments a una llista d'esdeveniments
     */
    public static List<EventEntity> parseEvents(String json, Boolean ourEvents) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        List data;
        if (ourEvents) data = objectMapper.readValue(json, new TypeReference<List<EventEntity>>() {});
        else data = objectMapper.readValue(json, new TypeReference<List<UnparsedEventEntity>>() {});

        List<EventEntity> ret = new ArrayList<>();

        UnparsedEventEntity ue;
        EventEntity e;
        for (int i = 0; i < data.size(); ++i) {
            if (ourEvents) {
                e = objectMapper.convertValue(data.get(i), EventEntity.class);
            } else {
                ue = objectMapper.convertValue(data.get(i), UnparsedEventEntity.class);
                EventLogic.parseNullValues(ue);
                EventLogic.processFields(ue);
                e = EventLogic.castUnparsedEventToParsedEvent(ue);
            }

            ret.add(e);
        }
        return ret;
    }

    /*
    * Descripció: Retorna en un array d'strings les quatre primeres imatges dels esdeveniments que conformen la llista
    */
    public static String[] getImages(List<EventEntity> l) {
        List<String> ret = new ArrayList<>();
        String img;
        String[] imgs;
        for (EventEntity e : l) {
            img = e.getImatges();
            if (img.contains(",")) {
                imgs = img.split(",");
                ret.addAll(Arrays.asList(imgs));
            } else {
                ret.add(img);
            }
        }

        return getFirst4Images(ret.toArray(new String[0]));
    }

    /*
    * Obté els primers quatre valors de l'array d'strings, o els que hi hagin si n'hi han menys
    */
    private static String[] getFirst4Images(String[] imgs) {
        if (imgs.length >= 4) {
            String[] ret = new String[4];
            System.arraycopy(imgs, 0, ret, 0, 4);
            return ret;
        }

        return imgs;
    }

    /*
    * Mapeja un esdeveniment de l'agenda culturla a un esdeveniment de domini
    */
    private static EventEntity castUnparsedEventToParsedEvent(UnparsedEventEntity ue) {
        EventEntity ret = new EventEntity();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {                                                                           // Ara a l'Agenda Cultural els hi ha donat per a
            ret.setDataFi(LocalDate.parse(ue.getDataFi(),format));                      // tenir dates buides
        } catch (DateTimeParseException e) {
            ret.setDataFi(LocalDate.parse("09-09-9999",format));
        }
        try {
            ret.setDataInici(LocalDate.parse(ue.getDataInici(),format));
        } catch (DateTimeParseException e) {
            ret.setDataInici(LocalDate.parse("09-09-9999",format));
        }
        ret.setId(ue.getId());
        ret.setDenominacio(ue.getDenominacio()); ret.setDescripcio(ue.getDescripcio()); ret.setPreu(ue.getPreu()); ret.setHorari(ue.getHorari());
        ret.setSubtitol(ue.getSubtitol()); ret.setAmbit(ue.getAmbit()); ret.setCategoria(ue.getCategoria()); ret.setAltresCategories(ue.getAltresCategories());
        ret.setLink(ue.getLink()); ret.setImatges(ue.getImatges()); ret.setAdreca(ue.getAdreca()); ret.setComarcaIMunicipi(ue.getComarcaIMunicipi());
        ret.setEmail(ue.getEmail()); ret.setEspai(ue.getEspai());
        if(!ue.getLatitud().isEmpty()) ret.setLatitud(Float.parseFloat(ue.getLatitud()));
        else ret.setLatitud(0.0f);
        if(!ue.getLongitud().isEmpty()) ret.setLongitud(Float.parseFloat(ue.getLongitud()));
        else ret.setLongitud(0.0f);
        ret.setTelefon(ue.getTelefon()); ret.setImgApp(ue.getImgApp()); ret.setScore(ue.getScore());
        return ret;
    }

    /*
    * Descripció: Seteja tots els valors nuls a strings buits
    */
    private static void parseNullValues(UnparsedEventEntity e) {
        if (e.getDenominacio() == null || e.getDenominacio().equals("null")) e.setDenominacio("");
        if (e.getDescripcio() == null || e.getDescripcio().equals("null")) e.setDescripcio("");
        if (e.getPreu() == null || e.getPreu().equals("null")) e.setPreu("");
        if (e.getSubtitol() == null || e.getSubtitol().equals("null")) e.setSubtitol("");
        if (e.getAmbit() == null || e.getAmbit().equals("null")) e.setAmbit("");
        if (e.getCategoria() == null || e.getCategoria().equals("null")) e.setCategoria("");
        if (e.getAltresCategories() == null || e.getAltresCategories().equals("null")) e.setAltresCategories("");
        if (e.getLink() == null || e.getLink().equals("null")) e.setLink("");
        if (e.getHorari() == null || e.getHorari().equals("null")) e.setHorari("");
        if (e.getImatges() == null || e.getImatges().equals("null")) e.setImatges("");
        if (e.getAdreca() == null || e.getAdreca().equals("null")) e.setAdreca("");
        if (e.getComarcaIMunicipi() == null || e.getComarcaIMunicipi().equals("null")) e.setComarcaIMunicipi("");
        if (e.getEmail() == null || e.getEmail().equals("null")) e.setEmail("");
        if (e.getEspai() == null || e.getEspai().equals("null")) e.setEspai("");
        if (e.getLatitud() == null || e.getLatitud().equals("null")) e.setLatitud("");
        if (e.getLongitud() == null || e.getLongitud().equals("null")) e.setLongitud("");
        if (e.getTelefon() == null || e.getTelefon().equals("null")) e.setTelefon("");
        if (e.getImgApp() == null || e.getImgApp().equals("null")) e.setImgApp("");
        if (e.getScore() == null) e.setScore(0.f);
    }

    /*
     * Descripció: Processa tots els camps que modifiquem per conveniència d'un esdeveniment
     */
    private static void processFields(UnparsedEventEntity e) {
        e.setDescripcio(processDescription(e.getDescripcio()));
        e.setDataInici(processDate(e.getDataInici()));
        e.setDataFi(processDate(e.getDataFi()));
        e.setAmbit(processTags(e.getAmbit()));
        e.setCategoria(processTags(e.getCategoria()));
        e.setAltresCategories(processTags(e.getAltresCategories()));
        e.setComarcaIMunicipi(processComarcaiMunicipi(e.getComarcaIMunicipi()));
    }

    /*
     * Descripció: Processa la descripció d'un esdeveniment treient-li les parts que corresponen a HTML
     */
    private static String processDescription(String desc) {
        if (desc == null) return "";
        return Jsoup.parse(desc).text().replaceAll("·", "");
    }

    /*
     * Descripció: Processa una data obtinguda de l'API de l'agenda cultural i la transforma en una data de tipus "dd-MM-yyyy".
     */
    private static String processDate(String data) {
        if (data == null) return "";
        String[] splittedDate = data.split("T"); // {"2017-12-31", "00:00:00.000"}
        String[] dateNewFormat = splittedDate[0].split("-"); // {"2017", "12", "31" }
        return dateNewFormat[2] + "-" + dateNewFormat[1] + "-" + dateNewFormat[0];
    }

    /*
     * Descripció: Processa totes les categories obtingudes de l'API de l'agenda cultural i les retorna en un set per evitar valors repetits.
     */
    public static Set<String> processCategories(List<String> content) {
        Set<String> categories = new HashSet<>();
        if (content == null) return categories;
        for (String s : content) {
            String[] splitted = s.split(",");
            for (String cat : splitted) {
                if (!cat.equals("") && !cat.equals("null")) {
                    categories.add(cat);
                }
            }
        }

        return categories;
    }

    /*
     * Descripció: Processa uns tags obtinguts de l'API de l'agenda cultural els hi treu la primera part "agenda:ambits/"
     */
    private static String processTags(String content) {
        if (content == null) return "";
        String[] ambits = content.split(",");
        String ret = "";
        for (String s : ambits) {
            if (!ret.equals("")) ret += ",";
            ret += s.substring(s.indexOf("/") + 1);
        }

        return ret;
    }

    /*
     * Descripció: Processa la comarca i municipi obtinguts de l'API de l'agenda cultural treient la part "agenda:ubicacions/"
     */
    private static String processComarcaiMunicipi(String content) {
        if (content == null || content.equals("")) return "";
        String ret;
        String[] comarcaMunicipi = content.split("/");
        if (comarcaMunicipi.length == 2 || comarcaMunicipi.length == 3)  ret = comarcaMunicipi[1];
        else ret = comarcaMunicipi[3] + "," + comarcaMunicipi[1];
        return ret;
    }
}
