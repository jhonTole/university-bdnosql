package dev.integrador.university.service;

import dev.integrador.university.entities.Event;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.repository.EventRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
}
