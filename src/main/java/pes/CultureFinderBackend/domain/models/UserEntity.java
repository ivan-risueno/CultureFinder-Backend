package pes.CultureFinderBackend.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {
    private String id;  // Necessari perquè l'id ens el dóna el frontend(el retorna el Firebase), no es genera automàticament a Dades
    @NotNull
    private String name;
    @NotBlank
    private String birthDate;
    private String profileImage;
    private String preferredCategories;
    private Boolean isAdmin;
}
