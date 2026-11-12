package dev.integrador.university.view;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.service.AttendeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;



public class NewAttendeeFormView implements Builder<Region> {
    private final AttendeeService service;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty documentNumber = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewAttendeeFormView(AttendeeService service){
        this.service = service;
    }

    private Node createNameField() {
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(name);
        return nameField;
    }

    private Node createDocumentNumberField() {
        TextField documentNumberField = new TextField();
        documentNumberField.textProperty().bindBidirectional(documentNumber);
        return documentNumberField;
    }

    private Node createEmailField() {
        TextField emailField = new TextField();
        emailField.textProperty().bindBidirectional(email);
        return emailField;
    }

    private Node createErrorMessageLabel(){
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    @Override
    public Region build() {
        Node nameField = createNameField();
        Node documentNumberField = createDocumentNumberField();
        Node emailField = createEmailField();

        Node errorMessageLabel = createErrorMessageLabel();

        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Nombre:"),
                nameField,
                new Label("Numero de documento"),
                documentNumberField,
                new Label("Correo:"),
                emailField,
                errorMessageLabel,
                submitButton
        );

        form.setSpacing(10);
        form.setPadding(new Insets(30)); // tamaño de la pestaña emergente

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Crear Asistente");

        submitButton.setOnAction(attendee -> {
            Attendee attendees = new Attendee();

            attendees.name = name.getValue();
            attendees.documentNumber = documentNumber.getValue();
            attendees.email = email.getValue();

            try {
                service.createAttendee(attendees);

                service.findAllAttendees();

                closeWindowFrom(attendee);

            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }

        });

        return submitButton;
    }

    private void closeWindowFrom(ActionEvent event){((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
