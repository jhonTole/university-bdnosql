package dev.integrador.university.view.event;

import dev.integrador.university.entities.Event;
import dev.integrador.university.service.EventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    private final StringProperty capacity = new SimpleStringProperty("");
    private final StringProperty startTime = new SimpleStringProperty("08:00");
    private final StringProperty endTime = new SimpleStringProperty("10:00");
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

    private Node createCapacityField() {
        TextField capacityField = new TextField();

        capacityField.textProperty().bindBidirectional(capacity);

        capacityField.setTextFormatter(
                new TextFormatter<>(change -> {
                    String text = change.getControlNewText();

                    if (text.matches("\\d*")) {
                        return change;
                    }

                    return null;
                })
        );
        // Este método permite escribir únicamente dígitos, por ejemplo 10, 50 o 200.
        // No permite escribir letras, decimales ni signos negativos.
        return capacityField;
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
        Node capacityField = createCapacityField();
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
                new Label("Capacidad:"),
                capacityField,
                new Label("Hora de inicio:"),
                createTimeCombo(startTime),
                new Label("Hora de fin:"),
                createTimeCombo(endTime),
                new Label("Fecha de Evento"),
                dateField,
                errorMessageLabel,
                submitButton
        );

        form.setSpacing(8); // Entrespaciado
        form.setPadding(new Insets(30, 25, 25, 30));// Entrespaciado

        form.setPrefWidth(400);  // Ancho del formulario
        Region espacio = new Region();
        espacio.setPrefHeight(20); // Alto del formulario

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Crear Evento");

        submitButton.setOnAction(event -> {
            try {
                Event events = new Event();

                events.name = name.getValue();
                events.description = description.getValue();
                events.category = category.getValue();
                events.startTime = startTime.getValue();
                events.endTime = endTime.getValue();
                events.capacity = Integer.valueOf(capacity.getValue().trim());
                events.date = convertLocalDateToDate(date);

                service.createEvent(events);

                service.findAllEvents();

                closeWindowFrom(event);

            } catch (NumberFormatException e) {
                errorMessage.setValue("Ingrese una capacidad numérica válida");

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
