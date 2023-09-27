package pes.CultureFinderBackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@IdClass(AssistanceId.class)
@Table(name="assistances")
public class Assistance {
    @Id
    private String userId;
    @Id
    private Long eventId;
}
