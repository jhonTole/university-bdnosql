package dev.integrador.university.view;

import dev.integrador.university.entities.Event;
import dev.integrador.university.model.EventModel;
import dev.integrador.university.service.EventService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
        /*
        Node newEventButton = createNewEventButton();
        Node deleteEventButton = createDeleteEventButton();
        Node updateEventButton = createUpdateEventButton();
         */
        HBox navigationBarHBox = new HBox();
        navigationBarHBox.setPadding(new Insets(8));
        navigationBarHBox.setSpacing(8);

        // VBox organiza su contenido verticalmente
        return new VBox(
                table,
                navigationBarHBox
        );
    }

    private Node createEventsTable() {
        TableView<Event> tableView = new TableView<>();

        // sincroniza la fila seleccionada con el modelo
        model.selectedEvent.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Event, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().eventsId));
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

        TableColumn<Event, String> dateColumn = new TableColumn<>("Fecha de creación");
        dateColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().date == null ? "" : p.getValue().date.toString()
        ));
        tableView.getColumns().add(dateColumn);

        tableView.setItems(model.events);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }
}