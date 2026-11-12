package dev.integrador.university.service;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.entities.Event;
import dev.integrador.university.model.AttendeeModel;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.repository.AttendeeRepository;
import dev.integrador.university.repository.EventRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

public class AttendeeService {

    private final AttendeeRepository repository;
    private final AttendeeModel model;

    public AttendeeService(AttendeeRepository repository, AttendeeModel model) {
        this.repository = repository;
        this.model = model;
    }

    public void findAllAttendees(){
        ObservableList<Attendee> attendees = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.attendees.set(attendees);
    }

    public void createAttendee(Attendee attendee){
        if (attendee.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }
        if(attendee.documentNumber.trim().isBlank()){
            throw new IllegalArgumentException("Numero de documento no valido");
        }
        if(attendee.email.trim().isBlank()){
            throw new IllegalArgumentException("Correo no valida");
        }
        repository.save(attendee);
    }

    public void removeAttendeeById(String attendeeId){
        if (repository.findById(attendeeId)== null){
            throw new NoSuchElementException("Evento no encontrado");
        }
        repository.removeAttendeeById(attendeeId);
    }

    public void updateAttendee(Attendee attendee){
        if (attendee.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (attendee.documentNumber.trim().isBlank()){
            throw new IllegalArgumentException("Descripcion no valida");
        }

        if (attendee.email.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }

        repository.update(attendee);
    }

}
