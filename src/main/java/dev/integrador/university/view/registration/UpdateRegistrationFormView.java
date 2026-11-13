package dev.integrador.university.view.registration;

import dev.integrador.university.entities.Registration;
import dev.integrador.university.service.RegistrationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdateRegistrationFormView implements Builder<Region> {
    private final RegistrationService service;
    private final Registration registrationToUpdate;

    private final StringProperty status = new SimpleStringProperty("");
    private LocalDate registrationDate = LocalDate.now();

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateRegistrationFormView(RegistrationService service, Registration registrationToUpdate) {
        this.service = service;
        this.registrationToUpdate = registrationToUpdate;

        status.setValue(registrationToUpdate.status);
        registrationDate = convertDateToLocalDate(registrationToUpdate.registrationDate);
    }

    private Node createTextField(StringProperty property) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property);
        return field;
    }

    private Node createRegistrationDateField() {
        DatePicker registrationDateField = new DatePicker();
        registrationDateField.setValue(registrationDate);
        registrationDateField.setMaxWidth(Double.MAX_VALUE);
        registrationDateField.setOnAction(registration ->
                registrationDate = registrationDateField.getValue());
        return registrationDateField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    @Override
    public Region build() {
        HBox buttons = new HBox(20, createSubmitButton(),createCancelButton());

        VBox form = new VBox(
                new Label("Estado:"),
                createTextField(status),
                new Label("Fecha de Registro:"),
                createRegistrationDateField(),
                createErrorMessageLabel(),
                buttons
        );

        form.setSpacing(8);
        form.setPadding(new Insets(30, 25, 25, 30));

        form.setPrefWidth(360);  // Ancho del formulario
        Region espacio = new Region();
        espacio.setPrefHeight(20);

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Guardar Cambios");
        submitButton.getStyleClass().add("btn-save");

        submitButton.setOnAction(registration -> {
            // Copia para no modificar el objeto de la tabla si algo falla
            Registration updated = new Registration();

            // replaceOneById reemplaza TODO el documento: conservamos
            // los campos que este formulario no edita
            updated.registrationId = registrationToUpdate.eventId;
            updated.attendeeId = registrationToUpdate.attendeeId;
            updated.eventId = registrationToUpdate.eventId;

            updated.status = status.getValue();
            updated.registrationDate = convertLocalDateToDate(registrationDate);

            try {
                service.updateRegistration(updated);
                service.findAllRegistrations();
                closeWindowFrom(registration);
            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }
        });

        return submitButton;
    }

    private Node createCancelButton() {
        Button button = new Button("Cancelar");
        button.getStyleClass().add("btn-cancel");

        button.setOnAction(event -> closeWindowFrom(event));

        return button;
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
