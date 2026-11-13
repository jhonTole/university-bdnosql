package dev.integrador.university.view.attendee;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.service.AttendeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;


public class UpdateAttendeeFormView implements Builder<Region> {
    private final AttendeeService service;
    private final Attendee attendeeToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty documentNumber = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateAttendeeFormView(AttendeeService service, Attendee attendeeToUpdate){

        this.service = service;
        this.attendeeToUpdate = attendeeToUpdate;

        name.setValue(attendeeToUpdate.name);
        documentNumber.setValue(attendeeToUpdate.documentNumber);
        email.setValue(attendeeToUpdate.email);
    }

    private Node createTextField(StringProperty property) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property);
        return field;
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
                new Label("Nombre:"),
                createTextField(name),
                new Label("Numero de Documento:"),
                createTextField(documentNumber),
                new Label("Correo:"),
                createTextField(email),
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

        submitButton.setOnAction(attendee -> {
            // Copia para no modificar el objeto de la tabla si algo falla
            Attendee updated = new Attendee();

            // replaceOneById reemplaza TODO el documento: conservamos
            // los campos que este formulario no edita
            updated.attendeeId = attendeeToUpdate.attendeeId;
            updated.name = name.getValue();
            updated.documentNumber = documentNumber.getValue();
            updated.email = email.getValue();

            try {
                service.updateAttendee(updated);
                service.findAllAttendees();
                closeWindowFrom(attendee);
            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }
        });

        return submitButton;
    }

    private Button createCancelButton() {
        Button button = new Button("Cancelar");
        button.getStyleClass().add("btn-cancel");

        button.setOnAction(event -> {
            Stage modal = (Stage) button.getScene().getWindow();
            modal.close();
        });

        return button;
    }

    private void closeWindowFrom(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
