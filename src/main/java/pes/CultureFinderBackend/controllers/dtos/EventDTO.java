package pes.CultureFinderBackend.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Long id;
    private LocalDate dataFi;
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
}
