package pes.CultureFinderBackend.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class
UserDTO {
    @NotNull
    private String name;
    @NotBlank
    private String birthDate;
    private String profileImage;
    private String preferredCategories;
    private Boolean isAdmin;
}