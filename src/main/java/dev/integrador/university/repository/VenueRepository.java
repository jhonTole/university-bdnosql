package dev.integrador.university.repository;

import com.mongodb.client.MongoDatabase;
import dev.integrador.university.entities.Venue;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

import java.util.ArrayList;
import java.util.List;

public class VenueRepository {
    private final String COLLECTION_NAME = "venues";
    private final JacksonMongoCollection<Venue> collection;

    public VenueRepository(MongoDatabase database){
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Venue.class,
                        UuidRepresentation.STANDARD
                );
    }

    public List<Venue> findAll(){return collection.find().into(new ArrayList<>());}
    public void save(Venue venue){collection.insert(venue);}
    public Venue findById(String id){return collection.findOneById(id);}
    public Long getCount() {return collection.countDocuments();}
    public void removeVenueById(String venueId){collection.removeById(venueId);}
    public void update(Venue venue){collection.replaceOneById(venue.venueId,venue);}
}
