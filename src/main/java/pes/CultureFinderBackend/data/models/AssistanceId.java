package pes.CultureFinderBackend.data.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class AssistanceId implements Serializable {
    private String userId;
    private Long eventId;

}
