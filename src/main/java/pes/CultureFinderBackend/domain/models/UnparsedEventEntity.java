package pes.CultureFinderBackend.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnparsedEventEntity {
    private Long id;
    @JsonProperty("data_fi")
    private String dataFi;
    @JsonProperty("data_inici")
    private String dataInici;
    @NotBlank
    @JsonProperty("denominaci")
    private String denominacio;
    @NotBlank
    @JsonProperty("descripcio")
    private String descripcio;
    @JsonProperty("entrades")
    private String preu;
    @JsonProperty("horari")
    private String horari;
    @JsonProperty("subt_tol")
    private String subtitol;
    @NotBlank
    @JsonProperty("tags_mbits")
    private String ambit;
    @JsonProperty("tags_categor_es")
    private String categoria;
    @JsonProperty("tags_altres_categor_es")
    private String altresCategories;
    @JsonProperty("enlla_os")
    private String link;
    @JsonProperty("imatges")
    private String imatges;
    @JsonProperty("adre_a")
    private String adreca;
    @JsonProperty("comarca_i_municipi")
    private String comarcaIMunicipi;
    @JsonProperty("email")
    private String email;
    @JsonProperty("espai")
    private String espai;
    @JsonProperty("latitud")
    private String latitud;
    @JsonProperty("longitud")
    private String longitud;
    @JsonProperty("tel_fon")
    private String telefon;
    @JsonProperty("imgapp")
    private String imgApp;
    @JsonProperty("score")
    private Float score;

    public UnparsedEventEntity() {}

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
                getTelefon().equals(that.getTelefon()) && getImgApp().equals(that.getImgApp()) && getScore().equals(that.getScore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataFi(), getDataInici(), getAmbit(), getCategoria(), getAltresCategories(), getLink(),
                getImatges(), getComarcaIMunicipi(), getEmail(), getLatitud(), getLongitud(), getTelefon(), getImgApp(), getScore());
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
                ",\nscore=" + score +
                "\n}";
    }
}
