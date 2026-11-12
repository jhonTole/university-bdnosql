package dev.integrador.university.service;

import dev.integrador.university.entities.Event;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.repository.EventRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class EventService {
    private final EventRepository repository;
    private final EventModel model;

    public EventService(EventRepository repository, EventModel model) {
        this.repository = repository;
        this.model = model;
    }

    public void findAllEvents(){
        ObservableList<Event> events = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.events.set(events);
    }
    public void createEvent(Event event){
        if (event.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }
        if(event.description.trim().isBlank()){
            throw new IllegalArgumentException("Descripcion no valida");
        }
        if(event.category.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }
        repository.save(event);
    }
    public void removeEventById(String eventId){
        if (repository.findById(eventId)== null){
            throw new NoSuchElementException("Evento no encontrado");
        }
        repository.removeEventById(eventId);
    }
    public void updateEvent(Event event){
        if (event.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (event.description.trim().isBlank()){
            throw new IllegalArgumentException("Genero no valido");
        }

        if (event.category.trim().isBlank()){
            throw new IllegalArgumentException("Author no valido");
        }

        repository.update(event);
    }
}
