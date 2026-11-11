package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Venue {
    @ObjectId
    @Id
    public String venueId;
    public String name;
    @Override
    public String toString(){
        return "Venue{" +
                "id=" + venueId + "," +
                "name=" + name + "}" ;
    }
}
