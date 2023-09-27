package pes.CultureFinderBackend.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListDTO {
    private Long id;
    private String userId;
    private String name;
    private String description;
    private Set<Long> events;
    private Integer nEvents;
    private String[] firstImages;
}
