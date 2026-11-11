package dev.integrador.university.entities;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Registration {
    @ObjectId
    @Id
    public String registrationId;
    public String name;
    @Override
    public String toString(){
        return "Registration{" +
                "id=" + registrationId + "," +
                "name=" + name + "}" ;
    }
}
