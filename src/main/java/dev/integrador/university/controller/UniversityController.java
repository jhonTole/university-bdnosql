package dev.integrador.university.controller;

import dev.integrador.university.model.UniversityModel;
import dev.integrador.university.repository.UniversityRepository;
import dev.integrador.university.view.UniversityView;
import javafx.scene.layout.Region;
import dev.integrador.university.view.service.UniversityService;

public class UniversityController {
    private final UniversityView view;
    private final UniversityService service;

    public UniversityController(UniversityRepository repository) {
        UniversityModel model = new UniversityModel();
        this.service = new UniversityService(repository, model);
        this.view = new UniversityView(model,service);
    }
    public Region getView(){
        service.findAllbooks();
        return view.build();
    }
}
