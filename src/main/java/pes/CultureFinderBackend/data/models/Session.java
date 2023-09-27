package pes.CultureFinderBackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="sessions")
public class Session {
    @Id
    private String userId;
    @NotNull
    private String token;
    private String deviceToken;
}
