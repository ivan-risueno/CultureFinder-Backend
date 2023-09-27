package pes.CultureFinderBackend.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListEntity {
    private Long id;
    private String userId;
    private String name;
    private String description;
    private Set<Long> events;

    public ListEntity() {}
}
