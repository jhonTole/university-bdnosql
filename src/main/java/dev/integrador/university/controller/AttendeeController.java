package dev.integrador.university.controller;

import dev.integrador.university.model.AttendeeModel;
import dev.integrador.university.repository.AttendeeRepository;
import dev.integrador.university.service.AttendeeService;
import dev.integrador.university.view.AttendeeView;
import javafx.scene.layout.Region;

public class AttendeeController {
    private final AttendeeView view;
    private final AttendeeService service;

    public AttendeeController(AttendeeRepository repository) {
        AttendeeModel model = new AttendeeModel();
        this.service = new AttendeeService(repository, model);
        this.view = new AttendeeView(model, service);
    }

    public Region getView(){
        service.findAllAttendees();
        return view.build();
    }
}
