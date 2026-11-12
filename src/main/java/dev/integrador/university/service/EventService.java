package dev.integrador.university.service;

import dev.integrador.university.entities.Event;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.repository.EventRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import java.util.NoSuchElementException;

public class EventService {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

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

    public void createEvent(Event event){
        if (event.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }
        if(event.description.trim().isBlank()){
            throw new IllegalArgumentException("Descripcion no valida");
        }
        if(event.category.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }
        repository.save(event);
    }

    public void removeEventById(String eventId){
        if (repository.findById(eventId)== null){
            throw new NoSuchElementException("Evento no encontrado");
        }
        repository.removeEventById(eventId);
    }

    public void updateEvent(Event event){
        if (event.name.trim().isBlank()){
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (event.description.trim().isBlank()){
            throw new IllegalArgumentException("Descripcion no valida");
        }

        if (event.category.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }
        if (event.startTime.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }
        if (event.endTime.trim().isBlank()){
            throw new IllegalArgumentException("Categoria no valida");
        }
        repository.update(event);
    }

    private LocalTime parseTime(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " es obligatoria");
        }
        try {
            return LocalTime.parse(value.trim(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " no válida, usa el formato HH:mm (ej. 08:30)");
        }
    }

    private void validateTimes(Event event) {
        LocalTime start = parseTime(event.startTime, "La hora de inicio");
        LocalTime end = parseTime(event.endTime, "La hora de fin");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio");
        }

        // Normaliza: "08:30" siempre se guarda igual
        event.startTime = start.format(TIME_FORMAT);
        event.endTime = end.format(TIME_FORMAT);
    }
}
