package dev.integrador.university.repository;

import com.mongodb.client.MongoDatabase;
import dev.integrador.university.entities.Event;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

public class UniversityRepository {
    private final String COLLECTION_NAME = "events";
    private final JacksonMongoCollection<Event> collection;
    public UniversityRepository(MongoDatabase database){
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Event.class,
                        UuidRepresentation.STANDARD
                );
    }
}
