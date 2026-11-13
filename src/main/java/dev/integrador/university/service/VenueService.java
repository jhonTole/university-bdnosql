package dev.integrador.university.service;

import dev.integrador.university.entities.Venue;
import dev.integrador.university.model.VenueModel;
import dev.integrador.university.repository.VenueRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class VenueService {

    private final VenueRepository repository;
    private final VenueModel model;

    public VenueService(VenueRepository repository, VenueModel model) {
        this.repository = repository;
        this.model = model;
    }

    public void findAllVenues(){
        ObservableList<Venue> venues = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.venues.set(venues);
    }

    public void createVenue(Venue venue){
        if (venue.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }
        if(venue.type.trim().isBlank()){
            throw new IllegalArgumentException("Tipo no valida");
        }
        if (venue.capacity == null || venue.capacity < 0) { // Esto evita guardar valores negativos. desde aqui puedes excluir el cero con "<="
            throw new IllegalArgumentException("La capacidad debe ser mayor o igual a cero");
        }
        repository.save(venue);
    }

    public void removeVenueById(String venueId){
        if (repository.findById(venueId)== null){
            throw new NoSuchElementException("Espacio no encontrado");
        }
        repository.removeVenueById(venueId);
    }

    public void updateVenue(Venue venue){
        if (venue.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (venue.type.trim().isBlank()){
            throw new IllegalArgumentException("Tipo no valido");
        }

        if (venue.capacity == null || venue.capacity < 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor o igual a cero");
        }
        repository.update(venue);
    }
}
