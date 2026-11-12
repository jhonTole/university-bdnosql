package dev.integrador.university.view;

import dev.integrador.university.entities.Event;
import dev.integrador.university.service.EventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.time.LocalDate;
import java.util.Date;

public class NewEventFormView implements Builder<Region> {
    private final EventService service;
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty category = new SimpleStringProperty("");
    private LocalDate date = LocalDate.now();

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewEventFormView(EventService service){
        this.service = service;
    }

    private Node createNameField() {
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(name);
        return nameField;
    }
    private Node createDescriptionField() {
        TextField descriptionField = new TextField();
        descriptionField.textProperty().bindBidirectional(description);
        return descriptionField;
    }
    private Node createCategoryField() {
        TextField categoryField = new TextField();
        categoryField.textProperty().bindBidirectional(category);
        return categoryField;
    }
    private Node createDateField() {
        DatePicker dateField = new DatePicker();
        dateField.setValue(date);
        dateField.setOnAction(event->{
            date = dateField.getValue();
        });
        return dateField;
    }
    private Node createErrorMessageLabel(){
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    @Override
    public Region build() {
        Node nameField = createNameField();
        Node descriptionField = createDescriptionField();
        Node categoryField = createCategoryField();
        Node dateField = createDateField();

        Node errorMessageLabel = createErrorMessageLabel();

        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Nombre:"),
                nameField,
                new Label("Descripcion"),
                descriptionField,
                new Label("Categoria:"),
                categoryField,
                new Label("Fecha de Evento"),
                dateField,
                errorMessageLabel,
                submitButton
        );

        form.setSpacing(8);
        form.setPadding(new Insets(8));

        return form;
    }
    private Node createSubmitButton() {
        Button submitButton = new Button("Crear Evento");

        submitButton.setOnAction(event -> {
            Event events = new Event();

            events.name = name.getValue();
            events.description = description.getValue();
            events.category = category.getValue();
            events.date = convertLocalDateToDate(date);

            try {
                service.createEvent(events);

                service.findAllEvents();

                closeWindowFrom(event);

            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }

        });

        return submitButton;
    }
    private void closeWindowFrom(ActionEvent event){
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    private Date convertLocalDateToDate(LocalDate input){
        return java.sql.Date.valueOf(input);
    }

}
