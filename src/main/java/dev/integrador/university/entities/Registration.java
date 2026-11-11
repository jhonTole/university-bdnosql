package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.Date;

public class Registration {
    @ObjectId
    @Id
    public String registrationId;

    @ObjectId
    public String attendeeId; // Mapeado a ObjectId real

    @ObjectId
    public String eventId;    // Mapeado a ObjectId real

    public Date registrationDate;
    public String status;     // "Activa", "Cancelada", etc.

    @Override
    public String toString() {
        return "Registration{" +
                "id='" + registrationId + '\'' +
                ", attendeeId='" + attendeeId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                '}';
    }
}
