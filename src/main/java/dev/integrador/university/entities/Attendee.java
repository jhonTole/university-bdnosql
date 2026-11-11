package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Attendee {
    @ObjectId
    @Id
    public String attendeeId;
    public String name;
    @Override
    public String toString(){
        return "Attendee{" +
                "id=" + attendeeId + "," +
                "name=" + name + "}" ;
    }
}
