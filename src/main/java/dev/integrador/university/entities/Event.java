package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.Date;

public class Event {
    @ObjectId
    @Id
    public String eventsId;
    public String name;
    public String description;
    public String category;
    public Date date; // antes createdAt
    public Date startTime;
    public Date endTime;
    public Integer capacity;
    @ObjectId // ahora se guarda como ObjectId real
    public String venueId;

    @Override
    public String toString(){

        return "Event{" +
                "id=" + eventsId + "," +
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
