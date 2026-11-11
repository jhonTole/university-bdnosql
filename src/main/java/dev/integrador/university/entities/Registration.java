package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.Date;

public class Registration {
    @ObjectId
    @Id
    public String registrationId;

    @ObjectId
    public String attendeeId;

    @ObjectId
    public String eventId;

    public Date registrationDate;
    public String status;

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
