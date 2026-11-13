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
    public void save(Event event){collection.insert(event);}
    public Event findById(String id){return collection.findOneById(id);}
    public Long getCount() {return collection.countDocuments();}
    public void removeEventById(String eventId){collection.removeById(eventId);}
    public void update(Event event){collection.replaceOneById(event.eventId,event);}
}
