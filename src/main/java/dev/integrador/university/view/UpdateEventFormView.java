package dev.integrador.university.view;

import dev.integrador.university.entities.Event;
import dev.integrador.university.service.EventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;
import javafx.util.Builder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdateEventFormView implements Builder<Region> {
    private final EventService service;
    private final Event eventToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty category = new SimpleStringProperty("");
    private final StringProperty startTime = new SimpleStringProperty("");
    private final StringProperty endTime = new SimpleStringProperty("");
    private LocalDate date = LocalDate.now();

    public UpdateEventFormView(EventService service, Event eventToUpdate){

        this.service = service;
        this.eventToUpdate = eventToUpdate;

        name.setValue(eventToUpdate.name);
        description.setValue(eventToUpdate.description);
        category.setValue(eventToUpdate.category);
        startTime.setValue(eventToUpdate.startTime);
        endTime.setValue(eventToUpdate.endTime);

        date = convertDateToLocalDate(eventToUpdate.date);
    }
    @Override
    public Region build() {
        return null;
    }

    private LocalDate convertDateToLocalDate(Date input){
        return LocalDate.ofInstant(input.toInstant(), ZoneId.systemDefault());
    }
}
