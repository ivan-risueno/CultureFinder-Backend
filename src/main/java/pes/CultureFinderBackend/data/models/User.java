package pes.CultureFinderBackend.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String birthDate;
    private String profileImage;
    private String preferredCategories;
    private Boolean isAdmin;
}
