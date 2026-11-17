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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

import java.time.LocalDate;
import java.util.Date;

public class NewEventFormView implements Builder<Region> {
    private final EventService service;
    private final List<Venue> venues;
    private final StringProperty venueId = new SimpleStringProperty("");

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty category = new SimpleStringProperty("");
    private final StringProperty capacity = new SimpleStringProperty("");
    private final StringProperty startTime = new SimpleStringProperty("08:00");
    private final StringProperty endTime = new SimpleStringProperty("10:00");
    private LocalDate date = LocalDate.now();

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewEventFormView(EventService service){
        this(service, java.util.Collections.emptyList());
    }

    public NewEventFormView(EventService service, List<Venue> venues) {
        this.service = service;
        this.venues = venues == null ? java.util.Collections.emptyList() : venues;
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
        // Permite que crezca con el ancho del formulario.
        dateField.setMaxWidth(Double.MAX_VALUE);
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
        Node nameField = createNameField();
        Node descriptionField = createDescriptionField();
        Node categoryField = createCategoryField();
        Node capacityField = createCapacityField();
        Node dateField = createDateField();

        // Agrupan texto y su campo
        VBox startGroup = new VBox(8, new Label("Fecha de Inicio:"), createTimeCombo(startTime));
        VBox endGroup = new VBox(8, new Label("Fecha de Fin:"), createTimeCombo(endTime));

        // Ambos reciben el mismo ancho.
        startGroup.setPrefWidth(0);
        endGroup.setPrefWidth(0);
        startGroup.setMaxWidth(Double.MAX_VALUE);
        endGroup.setMaxWidth(Double.MAX_VALUE);

        // Agrupa horizontalmente de Izquierda a Derecha
        HBox buttons = new HBox(16,createSubmitButton(),createCancelButton());
        HBox times = new HBox(16, startGroup,endGroup);

        // Ambos grupos aprovechan el espacio horizontal.
        HBox.setHgrow(startGroup, Priority.ALWAYS);
        HBox.setHgrow(endGroup, Priority.ALWAYS);
        Node errorMessageLabel = createErrorMessageLabel();

        VBox form = new VBox(
                new Label("Nombre:"),
                nameField,
                new Label("Descripcion"),
                descriptionField,
                new Label("Categoria:"),
                categoryField,
                new Label("Lugar del evento:"),
                createVenueField(),
                new Label("Capacidad:"),
                capacityField,
                times,
                new Label("Fecha de Evento"),
                dateField,
                errorMessageLabel,
                buttons
        );

        form.setSpacing(8); // Entrespaciado
        form.setPadding(new Insets(30, 25, 25, 30));// Entrespaciado
        form.setPrefWidth(400);  // Ancho del formulario

        return form;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("+ Añadir Evento");
        submitButton.getStyleClass().addAll("btn-create","sub-btn"); // addAll() para listado de estilos

        submitButton.setOnAction(event -> {
            try {
                if (venueId.get().isBlank()) {
                    errorMessage.set("Seleccione el lugar del evento.");
                    return;
                }
                Event events = new Event();

                events.venueId = venueId.get();
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
