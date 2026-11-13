package dev.integrador.university.repository;

import com.mongodb.client.MongoDatabase;
import dev.integrador.university.entities.Registration;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;

import java.util.ArrayList;
import java.util.List;

public class RegistrationRepository {
    private final String COLLECTION_NAME = "registrations";
    private final JacksonMongoCollection<Registration> collection;

    public RegistrationRepository(MongoDatabase database){
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Registration.class,
                        UuidRepresentation.STANDARD
                );
    }

    public List<Registration> findAll(){return collection.find().into(new ArrayList<>());}
    public void save(Registration registration){collection.insert(registration);}
    public Registration findById(String id){return collection.findOneById(id);}
    public Long getCount() {return collection.countDocuments();}
    public void removeRegistrationById(String registrationId){collection.removeById(registrationId);}
    public void update(Registration registration){collection.replaceOneById(registration.eventId,registration);}
}
