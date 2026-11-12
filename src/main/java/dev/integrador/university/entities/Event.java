package dev.integrador.university.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
// Sirve como un "escudo". Si alguien en el equipo agrega un campo nuevo a MongoDB
public class Event {
    @ObjectId
    @Id
    public String eventId;
    public String name;
    public String description;
    public String category;
    public Date date; // antes createdAt
    public String startTime;
    public String endTime;
    public Integer capacity;
    @ObjectId // ahora se guarda como ObjectId real
    public String venueId;

    @Override
    public String toString(){

        return "Event{" +
                "id=" + eventId + "," +
                "name=" + name + "," +
                "genre=" + category + "," +
                "author=" + description + "," +
                "createdAt=" + date + "," +
                "startTime=" + startTime + "," +
                "endTime=" + endTime + "," +
                "capacity=" + capacity + "," +
                "venueId=" + venueId +"}";
    }
}
