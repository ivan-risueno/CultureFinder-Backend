package pes.CultureFinderBackend.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name="events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataFi;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataInici;
    @NotBlank
    @Column(columnDefinition="TEXT")
    private String denominacio;
    @NotBlank
    @Column(columnDefinition="TEXT")
    private String descripcio;
    @Column(columnDefinition="TEXT")
    private String preu;
    @Column(columnDefinition="TEXT")
    private String horari;
    @Column(columnDefinition="TEXT")
    private String subtitol;
    @NotBlank
    @Column(columnDefinition="TEXT")
    private String ambit;
    @Column(columnDefinition="TEXT")
    private String categoria;
    @Column(columnDefinition="TEXT")
    private String altresCategories;
    @Column(columnDefinition="TEXT")
    private String link;
    @Column(columnDefinition="TEXT")
    private String imatges;
    @Column(columnDefinition="TEXT")
    private String adreca;
    @Column(columnDefinition="TEXT")
    private String comarcaIMunicipi;
    @Column(columnDefinition="TEXT")
    private String email;
    @Column(columnDefinition="TEXT")
    private String espai;

    private Float latitud;

    private Float longitud;
    @Column(columnDefinition="TEXT")
    private String telefon;
    @Column(columnDefinition="TEXT")
    private String imgApp;
    @ElementCollection
    @CollectionTable(name = "event_ratings",
            joinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "user_id")
    @Column(name = "score")
    private Map<String, Float> scores = new HashMap<>();

}
