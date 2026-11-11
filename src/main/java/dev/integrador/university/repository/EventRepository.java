package dev.integrador.university.repository;

import com.mongodb.client.MongoDatabase;
import dev.integrador.university.entities.Event;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final String COLLECTION_NAME = "events";
    private final JacksonMongoCollection<Event> collection;
    public EventRepository(MongoDatabase database){
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Event.class,
                        UuidRepresentation.STANDARD
                );
    }
    public List<Event> findAll(){return collection.find().into(new ArrayList<>());}
}
