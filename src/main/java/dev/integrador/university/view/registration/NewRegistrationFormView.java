package dev.integrador.university.view.registration;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Registration;
import dev.integrador.university.service.EventService;
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
import java.util.Date;

public class NewRegistrationFormView implements Builder<Region> {
    private final RegistrationService service;

    private final StringProperty status = new SimpleStringProperty("");
    private LocalDate registrationDate = LocalDate.now();

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewRegistrationFormView(RegistrationService service) {this.service = service;}

    private Node createStatusField() {
        TextField statusField = new TextField();
        statusField.textProperty().bindBidirectional(status);
        return statusField;
    }

    private Node createRegistrationDateField() {
        DatePicker registrationDateField = new DatePicker();
        registrationDateField.setValue(registrationDate);
        registrationDateField.setMaxWidth(Double.MAX_VALUE);
        registrationDateField.setOnAction(registration->{
            registrationDate = registrationDateField.getValue();
        });
        return registrationDateField;
    }

    private Node createErrorMessageLabel(){
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }


    @Override
    public Region build() {
        Node statusField = createStatusField();
        Node registrationDateField = createRegistrationDateField();

        Node errorMessageLabel = createErrorMessageLabel();

        HBox buttons = new HBox(20, createSubmitButton(),createCancelButton());


        VBox form = new VBox(
                new Label("Estado:"),
                statusField,
                new Label("Fecha de Inscripcion"),
                registrationDateField,
                errorMessageLabel,
                buttons
        );

        form.setSpacing(8);
        form.setPadding(new Insets(30, 25, 25, 30));

        form.setPrefWidth(360);  // Ancho del formulario
        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("+ Añadir Inscripcion");
        submitButton.getStyleClass().addAll("btn-create","sub-btn");

        submitButton.setOnAction(registration -> {
            Registration registrations = new Registration();

            registrations.status = status.getValue();
            registrations.registrationDate = convertLocalDateToDate(registrationDate);

            try {
                service.createRegistration(registrations);

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

    private void closeWindowFrom(ActionEvent event){
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    private Date convertLocalDateToDate(LocalDate input){
        return java.sql.Date.valueOf(input);
    }

}
