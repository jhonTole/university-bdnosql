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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class NewVenueFormView implements Builder<Region> {
    private final VenueService service;
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("");
    private final StringProperty capacity = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewVenueFormView(VenueService service) {this.service = service;}

    private Node createNameField() {
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(name);
        return nameField;
    }

    private Node createTypeField() {
        TextField descriptionField = new TextField();
        descriptionField.textProperty().bindBidirectional(type);
        return descriptionField;
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

    private Node createErrorMessageLabel(){
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    @Override
    public Region build() {

        Node nameField = createNameField();
        Node typeField = createTypeField();
        Node capacityField = createCapacityField();
        Node errorMessageLabel = createErrorMessageLabel();

        HBox buttons = new HBox(20, createSubmitButton(),createCancelButton());

        VBox form = new VBox(
                new Label("Nombre:"),
                nameField,
                new Label("Tipo"),
                typeField,
                new Label("Capacidad:"),
                capacityField,
                errorMessageLabel,
                buttons
        );

        form.setSpacing(8); // Entrespaciado
        form.setPadding(new Insets(30, 25, 25, 30)); // Entrespaciado

        form.setPrefWidth(400);  // Ancho del formulario

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("+ Añadir Espacio");
        submitButton.getStyleClass().addAll("btn-create","sub-btn");

        submitButton.setOnAction(venue -> {
        try {
            Venue venues = new Venue();

            venues.name = name.getValue();
            venues.type = type.getValue();
            venues.capacity = Integer.valueOf(capacity.getValue().trim());

            service.createVenue(venues);
            service.findAllVenues();

            closeWindowFrom(venue);

        } catch (NumberFormatException e) {
            errorMessage.setValue("Ingrese una capacidad numérica válida");

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

}
