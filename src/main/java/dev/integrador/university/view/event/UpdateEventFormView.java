package dev.integrador.university.view.event;

import dev.integrador.university.entities.Event;
import dev.integrador.university.service.EventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

    private final StringProperty errorMessage = new SimpleStringProperty("");

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

    private Node createTextField(StringProperty property) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property);
        return field;
    }

    private Node createTimeCombo(StringProperty property) {
        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            slots.add(String.format("%02d:00", h));
            slots.add(String.format("%02d:30", h));
        }
        ComboBox<String> combo = new ComboBox<>(slots);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.valueProperty().bindBidirectional(property);
        return combo;
    }

    private Node createDateField() {
        DatePicker dateField = new DatePicker();
        dateField.setValue(date);
        dateField.setOnAction(event -> date = dateField.getValue());
        return dateField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    @Override
    public Region build() {
        VBox form = new VBox(
                new Label("Nombre:"),
                createTextField(name),
                new Label("Descripción:"),
                createTextField(description),
                new Label("Categoría:"),
                createTextField(category),
                new Label("Fecha del evento:"),
                createDateField(),
                new Label("Hora de inicio:"),
                createTimeCombo(startTime),     // antes: createTextField(startTime)
                new Label("Hora de fin:"),
                createTimeCombo(endTime),
                createErrorMessageLabel(),
                createSubmitButton()
        );

        form.setSpacing(8);
        form.setPadding(new Insets(30, 25, 25, 30)); // Entrespaciado
        form.setPrefWidth(360);  // Ancho del formulario
        Region espacio = new Region();
        espacio.setPrefHeight(20);

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Actualizar Evento");

        submitButton.setOnAction(event -> {
            // Copia para no modificar el objeto de la tabla si algo falla
            Event updated = new Event();

            // replaceOneById reemplaza TODO el documento: conservamos
            // los campos que este formulario no edita
            updated.eventId = eventToUpdate.eventId;
            updated.capacity = eventToUpdate.capacity;
            updated.venueId = eventToUpdate.venueId;

            updated.name = name.getValue();
            updated.description = description.getValue();
            updated.category = category.getValue();
            updated.startTime = startTime.getValue();
            updated.endTime = endTime.getValue();
            updated.date = convertLocalDateToDate(date);

            try {
                service.updateEvent(updated);
                service.findAllEvents();
                closeWindowFrom(event);
            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }
        });

        return submitButton;
    }

    private void closeWindowFrom(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    private LocalDate convertDateToLocalDate(Date input) {
        if (input == null) {
            return LocalDate.now();
        }
        return LocalDate.ofInstant(input.toInstant(), ZoneId.systemDefault());
    }

    private Date convertLocalDateToDate(LocalDate input) {
        LocalDate value = (input == null) ? LocalDate.now() : input;
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
