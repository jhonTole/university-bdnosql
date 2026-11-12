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
        VBox form = new VBox(
                new Label("Nombre:"),
                createTextField(name),
                new Label("Numero de Documento:"),
                createTextField(documentNumber),
                new Label("Correo:"),
                createTextField(email),
                createErrorMessageLabel(),
                createSubmitButton()
        );

        form.setSpacing(8);
        form.setPadding(new Insets(30));
        form.setPrefWidth(360);

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Actualizar Asistente");

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

    private void closeWindowFrom(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
