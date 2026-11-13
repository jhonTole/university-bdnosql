package dev.integrador.university.controller;

import dev.integrador.university.model.RegistrationModel;
import dev.integrador.university.repository.RegistrationRepository;
import dev.integrador.university.service.RegistrationService;
import dev.integrador.university.view.registration.RegistrationView;
import javafx.scene.layout.Region;

public class RegistrationController {
    private final RegistrationView view;
    private final RegistrationService service;

    public RegistrationController(RegistrationRepository repository) {
        RegistrationModel model = new RegistrationModel();
        this.service = new RegistrationService(repository, model);
        this.view = new RegistrationView(model,service);
    }
    public Region getView(){
        service.findAllRegistrations();
        return view.build();
    }
}
