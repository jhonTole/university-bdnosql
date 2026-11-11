package dev.integrador.university.view;

import dev.integrador.university.model.UniversityModel;
import dev.integrador.university.view.service.UniversityService;

public class UniversityView {
    private final UniversityModel model;
    private final UniversityService service;

    public UniversityView(UniversityModel model, UniversityService service) {
        this.model = model;
        this.service = service;
    }
}
