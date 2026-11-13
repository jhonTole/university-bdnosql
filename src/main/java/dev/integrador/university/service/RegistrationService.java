package dev.integrador.university.service;

import dev.integrador.university.entities.Registration;
import dev.integrador.university.model.RegistrationModel;
import dev.integrador.university.repository.RegistrationRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class RegistrationService {
    private final RegistrationRepository repository;
    private final RegistrationModel model;

    public RegistrationService(RegistrationRepository repository, RegistrationModel model) {
        this.repository = repository;
        this.model = model;
    }

    public void findAllRegistrations(){
        ObservableList<Registration> registrations = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.registrations.set(registrations);
    }

    public void createRegistration(Registration registration){
        if (registration.status.trim().isBlank()){
            throw new IllegalArgumentException("Estado no valido");
        }

        repository.save(registration);
    }

    public void removeRegistrationById(String registrationId){
        if (repository.findById(registrationId)== null){
            throw new NoSuchElementException("Registro no encontrado");
        }
        repository.removeRegistrationById(registrationId);
    }

    public void updateRegistration(Registration registration){
        if (registration.status.trim().isBlank()){
            throw new IllegalArgumentException("Estado no valido");
        }
        repository.update(registration);
    }
}
