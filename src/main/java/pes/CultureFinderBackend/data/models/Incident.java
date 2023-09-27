package pes.CultureFinderBackend.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@Entity
@Table(name="incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;
    @NotNull
    private Long eventId;
    private String description;
    private String response;
    private Boolean isResolved;

}