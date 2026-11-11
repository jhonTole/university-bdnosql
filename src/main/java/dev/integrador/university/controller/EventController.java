package dev.integrador.university.controller;

import dev.integrador.university.model.EventModel;
import dev.integrador.university.repository.EventRepository;
import dev.integrador.university.view.EventView;
import javafx.scene.layout.Region;
import dev.integrador.university.service.EventService;

public class EventController {
    private final EventView view;
    private final EventService service;

    public EventController(EventRepository repository) {
        EventModel model = new EventModel();
        this.service = new EventService(repository, model);
        this.view = new EventView(model,service);
    }
    public Region getView(){
        service.findAllEvents();
        return view.build();
    }
}
