package pes.CultureFinderBackend.domain.models;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncidentEntity {
    @NotNull
    private Long id;

    private String userId;

    @NotNull
    private Long eventId;

    private String description;

    private String response;

    private Boolean isResolved;

}