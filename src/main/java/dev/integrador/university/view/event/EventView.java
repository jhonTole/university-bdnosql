package dev.integrador.university.view.event;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.service.EventService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;

public class EventView implements Builder<Region> {
    private final EventModel model;
    private final EventService service;

    public EventView(EventModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node table = createEventsTable();

        Node newEventButton = createNewEventButton();
        Node deleteEventButton = createDeleteEventButton();
        Node updateEventButton = createUpdateEventButton();

        HBox navigationBarHBox = new HBox(
                newEventButton,
                updateEventButton,
                deleteEventButton
        );

        navigationBarHBox.setPadding(new Insets(10));
        navigationBarHBox.setSpacing(8);

        // VBox organiza su contenido verticalmente
        VBox layout = new VBox(
                navigationBarHBox,
                table
        );

        // Permitir que la tabla crezca verticalmente
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    private Node createEventsTable() {
        TableView<Event> tableView = new TableView<>();

        // sincroniza la fila seleccionada con el modelo
        model.selectedEvent.bind(tableView.getSelectionModel().selectedItemProperty());

        TableColumn<Event, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().eventId));
        tableView.getColumns().add(idColumn);

        TableColumn<Event, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().name));
        tableView.getColumns().add(nameColumn);

        TableColumn<Event, String> categoryColumn = new TableColumn<>("Categoría");
        categoryColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().category));
        tableView.getColumns().add(categoryColumn);

        TableColumn<Event, String> descriptionColumn = new TableColumn<>("Descripción");
        descriptionColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().description));
        tableView.getColumns().add(descriptionColumn);

        TableColumn<Event, String> startColumn = new TableColumn<>("Inicio");
        startColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().startTime));
        tableView.getColumns().add(startColumn);

        TableColumn<Event, String> endColumn = new TableColumn<>("Fin");
        endColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().endTime));
        tableView.getColumns().add(endColumn);

        TableColumn<Event, String> capacityColumn = new TableColumn<>("Capacidad");
        capacityColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().capacity== null ? "" : p.getValue().capacity.toString()));
        tableView.getColumns().add(capacityColumn);

        TableColumn<Event, String> dateColumn = new TableColumn<>("Fecha");
        dateColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().date == null ? "" : p.getValue().date.toString()
        ));
        tableView.getColumns().add(dateColumn);

        tableView.setItems(model.events);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewEventModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Evento - Universidad");

        NewEventFormView newEventForm = new NewEventFormView(service);
        //Crea una instancia de scene para configurar el Style
        Scene scene = new Scene(newEventForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        // Asigna la escena configurada a la ventana
        model.setScene(scene);
        model.setMinWidth(300);
        return model;
    }

    private Node createNewEventButton(){
        Button button = new Button("+ Crear Evento");
        button.getStyleClass().add("btn-create");
        Stage newEventModal = createNewEventModal();
        button.setOnAction(event -> {
            newEventModal.showAndWait();
        });

        return button;
    }

    private Node createUpdateEventButton(){
        Button button = new Button("Actualizar Evento");
        button.getStyleClass().add("btn-update"); // asigna la clase btn-delete como en html "class=btn-update"
        button.disableProperty().bind(model.selectedEvent.isNull());
        button.setOnAction(event -> {
            Event selectedEvent = model.selectedEvent.get();
            Stage updateEventModal = createUpdateEventModal(selectedEvent);
            updateEventModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateEventModal(Event eventToUpdate){
        Stage modal = new Stage();
        // stage = ventana
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar Evento - Univesidad");

        UpdateEventFormView updateEventForm = new UpdateEventFormView(service, eventToUpdate);
        //Crea una instancia de scene para configurar el Style
        Scene scene = new Scene(updateEventForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        // Asigna la escena configurada a la ventana
        modal.setScene(scene);
        modal.setMinWidth(300);

        return modal;
    }

    private Node createDeleteEventButton(){
        Button button = new Button("Borrar");
        button.getStyleClass().add("btn-delete"); // asigna la clase btn-delete como en html "class=btn-delete"
        button.disableProperty().bind(model.selectedEvent.isNull());


        button.setOnAction(event -> {
            deleteEvent(model.selectedEvent.get().eventId);
            service.findAllEvents();
        });

        return button;
    }

    private void deleteEvent(String eventId){
        Event event = model.selectedEvent.get();
        service.removeEventById(event.eventId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Evento eliminado");
        successAlert.setHeaderText("Evento " + event.name + " eliminado exitosamente");
        successAlert.showAndWait();
    }
}