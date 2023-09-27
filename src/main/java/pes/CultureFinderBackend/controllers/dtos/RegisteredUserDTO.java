package pes.CultureFinderBackend.controllers.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisteredUserDTO {
    private String id;
    private String name;
    private String preferredCategories;
    private String birthDate;
    private Boolean isAdmin;
}
