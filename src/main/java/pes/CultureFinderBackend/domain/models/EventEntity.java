package pes.CultureFinderBackend.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventEntity {
    private Long id;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataFi;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataInici;
    @NotBlank
    private String denominacio;
    @NotBlank
    private String descripcio;
    private String preu;
    private String horari;
    private String subtitol;
    @NotBlank
    private String ambit;
    private String categoria;
    private String altresCategories;
    private String link;
    private String imatges;
    private String adreca;
    private String comarcaIMunicipi;
    private String email;
    private String espai;
    private Float latitud;
    private Float longitud;
    private String telefon;
    private String imgApp;
    private Float score;
    private Integer numberOfAssistants;

    public EventEntity() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return getDataFi().equals(that.getDataFi()) && getDataInici().equals(that.getDataInici()) &&
                getAmbit().equals(that.getAmbit()) && getCategoria().equals(that.getCategoria()) &&
                getAltresCategories().equals(that.getAltresCategories()) && getLink().equals(that.getLink()) &&
                getImatges().equals(that.getImatges()) && getComarcaIMunicipi().equals(that.getComarcaIMunicipi()) &&
                getEmail().equals(that.getEmail()) && getLatitud().equals(that.getLatitud()) && getLongitud().equals(that.getLongitud()) &&
                getTelefon().equals(that.getTelefon()) && getImgApp().equals(that.getImgApp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataFi(), getDataInici(), getAmbit(), getCategoria(), getAltresCategories(), getLink(),
                getImatges(), getComarcaIMunicipi(), getEmail(), getLatitud(), getLongitud(), getTelefon(), getImgApp(),
                getNumberOfAssistants());
    }

    @Override
    public String toString() {
        return "{" +
                "\nid=" + id +
                ",\ndataFi=" + dataFi +
                ",\ndataInici=" + dataInici +
                ",\ndenominacio=" + denominacio +
                ",\ndescripcio=" + descripcio +
                ",\npreu=" + preu +
                ",\nhorari=" + horari +
                ",\nsubtitol=" + subtitol +
                ",\nambit=" + ambit +
                ",\ncategoria=" + categoria +
                ",\naltresCategories=" + altresCategories +
                ",\nlink=" + link +
                ",\nimatges=" + imatges +
                ",\nadreca=" + adreca +
                ",\ncomarcaIMunicipi=" + comarcaIMunicipi +
                ",\nemail=" + email +
                ",\nespai=" + espai +
                ",\nlatitud=" + latitud +
                ",\nlongitud=" + longitud +
                ",\ntelefon=" + telefon +
                ",\nimgApp=" + imgApp +
                ",\nnumberOfAssistants=" + numberOfAssistants +
                "\n}";
    }
}
