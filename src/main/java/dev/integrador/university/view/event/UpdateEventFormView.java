package dev.integrador.university.view.event;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;
import java.util.List;
import dev.integrador.university.service.EventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdateEventFormView implements Builder<Region> {
    private final EventService service;
    private final List<Venue> venues;
    private final StringProperty venueId = new SimpleStringProperty("");
    private final Event eventToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty category = new SimpleStringProperty("");
    private final StringProperty startTime = new SimpleStringProperty("");
    private final StringProperty endTime = new SimpleStringProperty("");
    private final StringProperty capacity = new SimpleStringProperty("");
    private LocalDate date = LocalDate.now();

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateEventFormView(EventService service, Event eventToUpdate){

        this(service, eventToUpdate, java.util.Collections.emptyList());
    }

    public UpdateEventFormView(EventService service, Event eventToUpdate, List<Venue> venues) {
        this.service = service;
        this.venues = venues == null ? java.util.Collections.emptyList() : venues;
        this.eventToUpdate = eventToUpdate;
        venueId.set(eventToUpdate.venueId == null ? "" : eventToUpdate.venueId);

        name.setValue(eventToUpdate.name);
        description.setValue(eventToUpdate.description);
        category.setValue(eventToUpdate.category);
        startTime.setValue(eventToUpdate.startTime);
        endTime.setValue(eventToUpdate.endTime);
        capacity.setValue(eventToUpdate.capacity == null ? "" : eventToUpdate.capacity.toString());
        // convierte Integer a String
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

    private Node createDateField() {
        DatePicker dateField = new DatePicker();
        dateField.setValue(date);
        // Permite que crezca con el ancho del formulario.
        dateField.setMaxWidth(Double.MAX_VALUE);
        dateField.setOnAction(event -> date = dateField.getValue());
        return dateField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    private Node createVenueField() {
        ComboBox<Venue> combo = new ComboBox<>();
        combo.getItems().addAll(venues);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setPromptText(venues.isEmpty() ? "Cargue los espacios desde la aplicación" : "Seleccione un espacio");
        combo.setConverter(new javafx.util.StringConverter<Venue>() {
            @Override public String toString(Venue venue) {
                return venue == null ? "" : venue.name;
            }
            @Override public Venue fromString(String value) { return null; }
        });
        for (Venue venue : venues) {
            if (java.util.Objects.equals(venue.venueId, venueId.get())) {
                combo.setValue(venue);
                break;
            }
        }
        combo.valueProperty().addListener((obs, oldValue, newValue) ->
                venueId.set(newValue == null ? "" : newValue.venueId));
        return combo;
    }

    @Override
    public Region build() {
        // Agrupan texto y su campo
        VBox startGroup = new VBox(8, new Label("Fecha de Inicio:"), createTextField(startTime));
        VBox endGroup = new VBox(8, new Label("Fecha de Fin:"), createTextField(endTime));

        // Ambos reciben el mismo ancho.
        startGroup.setPrefWidth(0);
        endGroup.setPrefWidth(0);
        startGroup.setMaxWidth(Double.MAX_VALUE);
        endGroup.setMaxWidth(Double.MAX_VALUE);

        // Agrupa horizontalmente de Izquierda a Derecha
        HBox buttons = new HBox(20, createSubmitButton(),createCancelButton());
        HBox times = new HBox(16, startGroup,endGroup);

        // Ambos grupos aprovechan el espacio horizontal.
        HBox.setHgrow(startGroup, Priority.ALWAYS);
        HBox.setHgrow(endGroup, Priority.ALWAYS);

        VBox form = new VBox(
                new Label("Nombre:"),
                createTextField(name),
                new Label("Descripción:"),
                createTextField(description),
                new Label("Categoría:"),
                createTextField(category),
                new Label("Lugar del evento:"),
                createVenueField(),
                new Label("Capacidad:"),
                createCapacityField(),
                new Label("Fecha del evento:"),
                createDateField(),
                times,
                createErrorMessageLabel(),
                buttons
        );

        form.setSpacing(8);
        form.setPadding(new Insets(30, 25, 25, 30)); // Entrespaciado
        form.setPrefWidth(360);  // Ancho del formulario

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Guardar Cambios");
        submitButton.getStyleClass().add("btn-save");

        submitButton.setOnAction(event -> {
            try {// Copia para no modificar el objeto de la tabla si algo falla
                if (venueId.get().isBlank()) {
                    errorMessage.set("Seleccione el lugar del evento.");
                    return;
                }
                Event updated = new Event();

                // replaceOneById reemplaza TODO el documento: conservamos
                // los campos que este formulario no edita
                updated.eventId = eventToUpdate.eventId;
                updated.venueId = venueId.get();

                updated.name = name.getValue();
                updated.description = description.getValue();
                updated.category = category.getValue();
                updated.startTime = startTime.getValue();
                updated.endTime = endTime.getValue();
                // Convertir String a Integer
                updated.capacity = Integer.valueOf(capacity.getValue().trim());
                updated.date = convertLocalDateToDate(date);

                service.updateEvent(updated);
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
