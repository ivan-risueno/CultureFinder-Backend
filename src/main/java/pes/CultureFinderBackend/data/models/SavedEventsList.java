package pes.CultureFinderBackend.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="lists")
public class SavedEventsList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String userId;
    @NotBlank
    private String name;
    @Column(columnDefinition="TEXT")
    private String description;
    @ElementCollection
    private Set<Long> events = new HashSet<>();
}
