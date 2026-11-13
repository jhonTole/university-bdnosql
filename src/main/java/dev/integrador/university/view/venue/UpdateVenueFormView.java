package dev.integrador.university.view.venue;

import dev.integrador.university.entities.Venue;
import dev.integrador.university.service.VenueService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class UpdateVenueFormView implements Builder<Region> {
    private final VenueService service;
    private final Venue venueToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("");
    private final StringProperty capacity = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateVenueFormView(VenueService service, Venue venueToUpdate) {
        this.service = service;
        this.venueToUpdate = venueToUpdate;

        name.setValue(venueToUpdate.name);
        type.setValue(venueToUpdate.type);
        capacity.setValue(venueToUpdate.capacity == null ? "" : venueToUpdate.capacity.toString());
        // convierte Integer a String
    }

    private Node createTextField(StringProperty property) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property);
        return field;
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

        return capacityField;
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
                createTextField(type),
                new Label("Capacity:"),
                createCapacityField(),
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
        Button submitButton = new Button("Actualizar Espacio");

        submitButton.setOnAction(venue -> {
            try {
                Venue updated = new Venue(); // Crear una copia para no modificar el objeto original                                         // Crear una copia para no modificar el objeto original
                updated.venueId = venueToUpdate.venueId;

                updated.name = name.getValue();
                updated.type = type.getValue();

                // Convertir String a Integer
                updated.capacity = Integer.valueOf(capacity.getValue().trim());

                service.updateVenue(updated);

                service.findAllVenues();

                closeWindowFrom(venue);

            } catch (NumberFormatException e) {
                errorMessage.setValue(
                        "Ingrese una capacidad numérica válida"
                );

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
