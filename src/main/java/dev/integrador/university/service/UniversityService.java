package dev.integrador.university.view.service;

import dev.integrador.university.model.UniversityModel;
import dev.integrador.university.repository.UniversityRepository;

public class UniversityService {
    private final UniversityRepository repository;
    private final UniversityModel model;

    public UniversityService(UniversityRepository repository, UniversityModel model) {
        this.repository = repository;
        this.model = model;
    }
}
