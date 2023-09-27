package pes.CultureFinderBackend.controllers.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDTO {
    @NotNull
    private Long id;

    private String userId;

    @NotNull
    private Long eventId;

    private String description;

    private String response;

    private Boolean isResolved;
}
